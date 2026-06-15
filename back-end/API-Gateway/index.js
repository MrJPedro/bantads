const express = require('express');
const cors = require('cors');
const { createProxyMiddleware } = require('http-proxy-middleware');
const axios = require('axios');
const jwt = require('jsonwebtoken');
const swaggerUi = require('swagger-ui-express');

const app = express();
const PORT = process.env.PORT;
const invalidatedTokens = new Set();

// ─── OpenAPI 3.0 Spec ────────────────────────────────────────────────────────
const swaggerSpec = {
  openapi: '3.0.0',
  info: {
    title: 'BANTADS API Gateway',
    version: '1.0.0',
    description:
      'Ponto de entrada único do sistema BANTADS. ' +
      'Use o botão **Authorize** para informar o Bearer token obtido no endpoint `/login`.',
  },
  servers: [{ url: `http://localhost:${process.env.PORT || 3000}` }],
  components: {
    securitySchemes: {
      BearerAuth: {
        type: 'http',
        scheme: 'bearer',
        bearerFormat: 'JWT',
      },
    },
    schemas: {
      LoginRequest: {
        type: 'object',
        required: ['login', 'senha'],
        properties: {
          login: { type: 'string', example: 'cliente@email.com' },
          senha: { type: 'string', format: 'password', example: 'senha123' },
        },
      },
      LoginResponse: {
        type: 'object',
        properties: {
          access_token: { type: 'string' },
          token_type:   { type: 'string', example: 'bearer' },
          tipo:         { type: 'string', enum: ['CLIENTE', 'GERENTE', 'ADMINISTRADOR'] },
          usuario: {
            type: 'object',
            properties: {
              nome:  { type: 'string' },
              cpf:   { type: 'string' },
              email: { type: 'string' },
            },
          },
        },
      },
      Error: {
        type: 'object',
        properties: { erro: { type: 'string' } },
      },
    },
  },
  security: [{ BearerAuth: [] }],
  tags: [
    { name: 'Auth',     description: 'Autenticação' },
    { name: 'Clientes', description: 'Gestão de clientes' },
    { name: 'Contas',   description: 'Operações bancárias' },
    { name: 'Gerentes', description: 'Gestão de gerentes' },
    { name: 'Sistema',  description: 'Operações administrativas do sistema' },
  ],
  paths: {
    '/login': {
      post: {
        tags: ['Auth'],
        summary: 'Autenticar usuário',
        security: [],
        requestBody: {
          required: true,
          content: { 'application/json': { schema: { $ref: '#/components/schemas/LoginRequest' } } },
        },
        responses: {
          200: { description: 'Login bem-sucedido', content: { 'application/json': { schema: { $ref: '#/components/schemas/LoginResponse' } } } },
          401: { description: 'Credenciais inválidas', content: { 'application/json': { schema: { $ref: '#/components/schemas/Error' } } } },
        },
      },
    },
    '/clientes': {
      get: {
        tags: ['Clientes'],
        summary: 'Listar clientes',
        description:
          '- `?filtro=para_aprovar` — pendentes de aprovação (GERENTE)\n' +
          '- `?filtro=melhores_clientes` — top 3 por saldo (GERENTE)\n' +
          '- `?filtro=adm_relatorio_clientes` — relatório completo (ADMINISTRADOR)',
        parameters: [
          { in: 'query', name: 'filtro', schema: { type: 'string', enum: ['para_aprovar', 'melhores_clientes', 'adm_relatorio_clientes'] } },
        ],
        responses: {
          200: { description: 'Lista de clientes' },
          403: { description: 'Acesso negado', content: { 'application/json': { schema: { $ref: '#/components/schemas/Error' } } } },
        },
      },
      post: {
        tags: ['Clientes'],
        summary: 'Cadastrar novo cliente (público)',
        security: [],
        requestBody: { required: true, content: { 'application/json': { schema: { type: 'object' } } } },
        responses: {
          201: { description: 'Cliente criado' },
          400: { description: 'Dados inválidos' },
        },
      },
    },
    '/clientes/{cpf}': {
      get: {
        tags: ['Clientes'],
        summary: 'Detalhe do cliente (GERENTE / ADMINISTRADOR)',
        parameters: [{ in: 'path', name: 'cpf', required: true, schema: { type: 'string' } }],
        responses: {
          200: { description: 'Dados compostos do cliente (cliente + conta + gerente)' },
          403: { description: 'Acesso negado' },
          404: { description: 'Não encontrado' },
        },
      },
      put: {
        tags: ['Clientes'],
        summary: 'Atualizar perfil do próprio cliente (CLIENTE)',
        parameters: [{ in: 'path', name: 'cpf', required: true, schema: { type: 'string' } }],
        requestBody: { required: true, content: { 'application/json': { schema: { type: 'object' } } } },
        responses: {
          200: { description: 'Cliente atualizado' },
          403: { description: 'Acesso negado' },
        },
      },
    },
    '/clientes/{cpf}/perfil': {
      get: {
        tags: ['Clientes'],
        summary: 'Perfil composto do próprio cliente (CLIENTE)',
        parameters: [{ in: 'path', name: 'cpf', required: true, schema: { type: 'string' } }],
        responses: {
          200: { description: 'Dados compostos: cliente + conta + gerente' },
          403: { description: 'Acesso negado' },
        },
      },
    },
    '/clientes/{cpf}/aprovar': {
      post: {
        tags: ['Clientes'],
        summary: 'Aprovar cadastro de cliente (GERENTE)',
        parameters: [{ in: 'path', name: 'cpf', required: true, schema: { type: 'string' } }],
        responses: {
          200: { description: 'Cliente aprovado' },
          403: { description: 'Acesso negado' },
        },
      },
    },
    '/clientes/{cpf}/rejeitar': {
      post: {
        tags: ['Clientes'],
        summary: 'Rejeitar cadastro de cliente (GERENTE)',
        parameters: [{ in: 'path', name: 'cpf', required: true, schema: { type: 'string' } }],
        responses: {
          200: { description: 'Cliente rejeitado' },
          403: { description: 'Acesso negado' },
        },
      },
    },
    '/contas/{cpf}/saldo': {
      get: {
        tags: ['Contas'],
        summary: 'Consultar saldo (CLIENTE — conta própria)',
        parameters: [{ in: 'path', name: 'cpf', required: true, schema: { type: 'string' }, description: 'Número da conta' }],
        responses: {
          200: { description: 'Saldo atual' },
          403: { description: 'Acesso negado' },
        },
      },
    },
    '/contas/{cpf}/extrato': {
      get: {
        tags: ['Contas'],
        summary: 'Consultar extrato (CLIENTE — conta própria)',
        parameters: [{ in: 'path', name: 'cpf', required: true, schema: { type: 'string' }, description: 'Número da conta' }],
        responses: {
          200: { description: 'Extrato de transações' },
          403: { description: 'Acesso negado' },
        },
      },
    },
    '/contas/{cpf}/depositar': {
      post: {
        tags: ['Contas'],
        summary: 'Depositar (CLIENTE — conta própria)',
        parameters: [{ in: 'path', name: 'cpf', required: true, schema: { type: 'string' }, description: 'Número da conta' }],
        requestBody: { required: true, content: { 'application/json': { schema: { type: 'object', properties: { valor: { type: 'number' } } } } } },
        responses: {
          200: { description: 'Depósito realizado' },
          403: { description: 'Acesso negado' },
        },
      },
    },
    '/contas/{cpf}/sacar': {
      post: {
        tags: ['Contas'],
        summary: 'Sacar (CLIENTE — conta própria)',
        parameters: [{ in: 'path', name: 'cpf', required: true, schema: { type: 'string' }, description: 'Número da conta' }],
        requestBody: { required: true, content: { 'application/json': { schema: { type: 'object', properties: { valor: { type: 'number' } } } } } },
        responses: {
          200: { description: 'Saque realizado' },
          403: { description: 'Acesso negado' },
        },
      },
    },
    '/contas/{cpf}/transferir': {
      post: {
        tags: ['Contas'],
        summary: 'Transferir (CLIENTE — conta própria)',
        parameters: [{ in: 'path', name: 'cpf', required: true, schema: { type: 'string' }, description: 'Número da conta' }],
        requestBody: {
          required: true,
          content: { 'application/json': { schema: { type: 'object', properties: { contaDestino: { type: 'string' }, valor: { type: 'number' } } } } },
        },
        responses: {
          200: { description: 'Transferência realizada' },
          403: { description: 'Acesso negado' },
        },
      },
    },
    '/contas/top3': {
      get: {
        tags: ['Contas'],
        summary: 'Top 3 clientes por saldo (GERENTE)',
        responses: {
          200: { description: 'Lista dos 3 melhores clientes' },
          403: { description: 'Acesso negado' },
        },
      },
    },
    '/gerentes': {
      get: {
        tags: ['Gerentes'],
        summary: 'Listar gerentes / dashboard',
        description: '- Sem parâmetros: lista todos os gerentes (ADMINISTRADOR)\n- `?numero=dashboard`: dashboard de gerentes com saldos (GERENTE / ADMINISTRADOR)',
        parameters: [{ in: 'query', name: 'numero', schema: { type: 'string', enum: ['dashboard'] } }],
        responses: {
          200: { description: 'Lista ou dashboard de gerentes' },
          403: { description: 'Acesso negado' },
        },
      },
      post: {
        tags: ['Gerentes'],
        summary: 'Criar gerente (ADMINISTRADOR)',
        requestBody: { required: true, content: { 'application/json': { schema: { type: 'object' } } } },
        responses: {
          201: { description: 'Gerente criado' },
          403: { description: 'Acesso negado' },
        },
      },
    },
    '/gerentes/{cpf}': {
      get: {
        tags: ['Gerentes'],
        summary: 'Detalhe do gerente (ADMINISTRADOR)',
        parameters: [{ in: 'path', name: 'cpf', required: true, schema: { type: 'string' } }],
        responses: { 200: { description: 'Dados do gerente' }, 403: { description: 'Acesso negado' } },
      },
      put: {
        tags: ['Gerentes'],
        summary: 'Atualizar gerente (ADMINISTRADOR)',
        parameters: [{ in: 'path', name: 'cpf', required: true, schema: { type: 'string' } }],
        requestBody: { required: true, content: { 'application/json': { schema: { type: 'object' } } } },
        responses: { 200: { description: 'Gerente atualizado' }, 403: { description: 'Acesso negado' } },
      },
      delete: {
        tags: ['Gerentes'],
        summary: 'Remover gerente (ADMINISTRADOR)',
        parameters: [{ in: 'path', name: 'cpf', required: true, schema: { type: 'string' } }],
        responses: { 200: { description: 'Gerente removido' }, 403: { description: 'Acesso negado' } },
      },
    },
    '/gerentes/{cpf}/clientes': {
      get: {
        tags: ['Gerentes'],
        summary: 'Clientes do gerente (GERENTE)',
        parameters: [{ in: 'path', name: 'cpf', required: true, schema: { type: 'string' } }],
        responses: { 200: { description: 'Lista de clientes com saldo e limite' }, 403: { description: 'Acesso negado' } },
      },
    },
    '/reboot': {
      get: {
        tags: ['Sistema'],
        summary: 'Reiniciar todos os microsserviços para o estado inicial (público)',
        security: [],
        responses: {
          200: { description: 'Todos os serviços reiniciados com sucesso' },
          207: { description: 'Reboot parcial — alguns serviços falharam' },
        },
      },
    },
  },
};
// ─────────────────────────────────────────────────────────────────────────────

function configFrom(req) {
    return {
        headers: { Authorization: req.headers['authorization'] },
        timeout: 5000
    };
}

function isDashboardRequest(req) {
    return req.query.numero === 'dashboard' || req.query.filtro === 'dashboard';
}

function withProxyPrefix(prefix) {
    return (path) => {
        if (path === prefix || path.startsWith(`${prefix}/`) || path.startsWith(`${prefix}?`)) {
            return path;
        }
        if (path === '/') {
            return prefix;
        }
        if (path.startsWith('/?')) {
            return `${prefix}${path.substring(1)}`;
        }
        return `${prefix}${path}`;
    };
}

async function buscarGerente(cpf, config) {
    if (!cpf) {
        return null;
    }

    try {
        const { data } = await axios.get(`${process.env.GERENTES_URI}/gerentes/${cpf}`, config);
        return data;
    } catch (err) {
        console.error(`Falha ao buscar gerente ${cpf}:`, err.message);
        return null;
    }
}

async function comporClienteComConta(cliente, conta, config) {
    const gerente = await buscarGerente(conta?.gerente, config);

    return {
        cpf: cliente.cpf,
        nome: cliente.nome,
        telefone: cliente.telefone,
        email: cliente.email,
        salario: cliente.salario,
        endereco: cliente.endereco,
        cep: cliente.cep,
        cidade: cliente.cidade,
        estado: cliente.estado,
        conta: conta?.numero ?? null,
        saldo: conta?.saldo ?? null,
        limite: conta?.limite ?? null,
        gerente: gerente?.cpf ?? conta?.gerente ?? cliente.gerenteCpf ?? null,
        gerente_cpf: gerente?.cpf ?? conta?.gerente ?? cliente.gerenteCpf ?? null,
        gerente_nome: gerente?.nome ?? null,
        gerente_email: gerente?.email ?? null
    };
}

async function buscarContaPorCliente(cpf, config) {
    try {
        const { data } = await axios.get(`${process.env.CONTAS_URI}/contas/cliente/${cpf}`, config);
        return data;
    } catch (err) {
        console.error(`Falha ao buscar conta do cliente ${cpf}:`, err.message);
        return null;
    }
}

async function transferirClienteParaNovoGerente(cpfNovoGerente, config) {
    const { data: gerentes } = await axios.get(`${process.env.GERENTES_URI}/gerentes`, config);

    const candidatos = await Promise.all(
        gerentes
            .filter(gerente => gerente.cpf !== cpfNovoGerente)
            .map(async (gerente) => {
                const { data: contas } = await axios.get(`${process.env.CONTAS_URI}/contas/gerente/${gerente.cpf}`, config);
                return { gerente, contas };
            })
    );

    const doador = candidatos
        .filter(candidato => candidato.contas.length > 0)
        .sort((a, b) =>
            b.contas.length - a.contas.length ||
            b.gerente.nome.localeCompare(a.gerente.nome, 'pt-BR', { sensitivity: 'base' })
        )[0];

    if (!doador) {
        return;
    }

    const conta = doador.contas[0];
    await axios.put(`${process.env.CONTAS_URI}/contas/${conta.numero}/gerente`, { gerente: cpfNovoGerente }, config);
    await axios.put(`${process.env.CLIENTES_URI}/clientes/${conta.cliente}/gerente`, { gerenteCpf: cpfNovoGerente }, config);
}

app.use(cors({
    origin: process.env.FRONT, 
    methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS', 'PATCH'],
    allowedHeaders: ['*'], 
    credentials: true     
}));

function verifyJWT(req, res, next) {
  if (req.method === 'OPTIONS') {
    return next();
  }
  if (req.path === '/login' && req.method === 'POST') {
    return next();
  }
  if (req.path === '/clientes' && req.method === 'POST') {
    return next();
  }
  if (req.path === '/reboot' && req.method === 'GET') {
    return next();
  }
  if (req.path.startsWith('/api-docs')) {
    return next();
  }

  const authHeader = req.headers['authorization'];
  if (!authHeader) return res.status(401).json({ erro: 'Token não fornecido' });
  
  const token = authHeader.split(' ')[1]; // "Bearer XXXXX"
  if (!token) return res.status(401).json({ erro: 'Token mal formatado' });
  if (invalidatedTokens.has(token)) return res.status(401).json({ erro: 'Token inválido ou expirado' });
  
  jwt.verify(token, process.env.SECRET, (err, decoded) => {
    if (err) return res.status(401).json({ erro: 'Token inválido ou expirado' });
    req.user = decoded; // { login, tipo, cpf }
    next();
  });
}

function autorizar(...tiposPermitidos) {
  return (req, res, next) => {
    if (!req.user || !tiposPermitidos.includes(req.user.tipo)) {
      return res.status(403).json({ erro: 'Acesso negado' });
    }
    next();
  };
}

// ─── Swagger UI (público) ────────────────────────────────────────────────────
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerSpec, {
  customSiteTitle: 'BANTADS API Docs',
  swaggerOptions: { persistAuthorization: true },
}));
// ─────────────────────────────────────────────────────────────────────────────

app.use(verifyJWT);

app.all([
    '/clientes/:cpf',
    '/clientes/:cpf/perfil',
    '/contas/:cpf/depositar',
    '/contas/:cpf/sacar',
    '/contas/:cpf/transferir',
    '/contas/:cpf/extrato',
    '/contas/:cpf/saldo'
], async (req, res, next) => {
    if (req.user && req.user.tipo === 'CLIENTE') {
        
        // Função auxiliar para manter apenas os números
        const limparNumeros = (str) => str ? String(str).replace(/\D/g, '') : '';

        // Limpa a pontuação tanto do parâmetro da rota quanto do usuário logado
        const recurso = limparNumeros(req.params.cpf);
        const userCpf = limparNumeros(req.user.cpf);

        if (req.path.startsWith('/clientes/')) {
            if (recurso && userCpf && recurso !== userCpf) {
                return res.status(403).json({ erro: 'Acesso negado: recurso não pertence ao usuário' });
            }
        } else if (req.path.startsWith('/contas/')) {
            try {
                const config = configFrom(req);
                
                const conta = await buscarContaPorCliente(userCpf, config); 
                
                if (!conta || limparNumeros(conta.numero) !== recurso) {
                    return res.status(403).json({ erro: 'Acesso negado: recurso não pertence ao usuário' });
                }
            } catch (err) {
                return res.status(403).json({ erro: 'Acesso negado: erro ao verificar a conta' });
            }
        }
    }
    next();
});

app.put('/clientes/:cpf', autorizar('CLIENTE'), (req, res, next) => next());
app.post('/contas/:cpf/depositar', autorizar('CLIENTE'), (req, res, next) => next());
app.post('/contas/:cpf/sacar', autorizar('CLIENTE'), (req, res, next) => next());
app.post('/contas/:cpf/transferir', autorizar('CLIENTE'), (req, res, next) => next());
app.get('/contas/:cpf/extrato', autorizar('CLIENTE'), (req, res, next) => next());
app.get('/contas/:cpf/saldo', autorizar('CLIENTE'), (req, res, next) => next());

app.post('/clientes/:cpf/aprovar', autorizar('GERENTE'), (req, res, next) => next());
app.post('/clientes/:cpf/rejeitar', autorizar('GERENTE'), (req, res, next) => next());


// Rota: Relatório do Administrador (Composição de Clientes + Conta + Gerente)
app.get('/clientes', async (req, res, next) => {
    const { filtro } = req.query;

    if (filtro === 'para_aprovar' || filtro === 'melhores_clientes') {
        if (!req.user || req.user.tipo !== 'GERENTE') {
            return res.status(403).json({ erro: 'Acesso negado' });
        }
    } else if (filtro === 'adm_relatorio_clientes') {
        if (!req.user || req.user.tipo !== 'ADMINISTRADOR') {
            return res.status(403).json({ erro: 'Acesso negado' });
        }
    }

    if (filtro === 'melhores_clientes') {
        try {
            const config = configFrom(req);
            const { data: top3Contas } = await axios.get(`${process.env.CONTAS_URI}/contas/top3`, config);

            const clientesPromises = top3Contas.map(async (conta) => {
                try {
                    const { data: cliente } = await axios.get(`${process.env.CLIENTES_URI}/clientes/${conta.cliente}`, config);
                    return {
                        cpf: cliente.cpf,
                        nome: cliente.nome,
                        cidade: cliente.cidade,
                        estado: cliente.estado,
                        saldo: conta.saldo
                    };
                } catch (err) {
                    console.error(`Falha ao buscar dados do cliente ${conta.cliente}:`, err.message);
                    return null;
                }
            });

            const top3Compostos = (await Promise.all(clientesPromises))
                .filter(c => c !== null)
                .sort((a, b) => b.saldo - a.saldo)
                .slice(0, 3);

            return res.json(top3Compostos);
        } catch (error) {
            console.error('Erro ao listar top 3 clientes:', error.message);
            return res.status(500).json({ erro: 'Falha ao listar top 3 clientes' });
        }
    }

    if (!filtro && req.user?.tipo === 'GERENTE') {
        try {
            const config = configFrom(req);
            const { data: contas } = await axios.get(`${process.env.CONTAS_URI}/contas/gerente/${req.user.cpf}`, config);

            const clientesPromises = contas.map(async (conta) => {
                try {
                    const { data: cliente } = await axios.get(`${process.env.CLIENTES_URI}/clientes/${conta.cliente}`, config);
                    return {
                        cpf: cliente.cpf,
                        nome: cliente.nome,
                        telefone: cliente.telefone,
                        email: cliente.email,
                        salario: cliente.salario,
                        endereco: cliente.endereco,
                        cep: cliente.cep,
                        cidade: cliente.cidade,
                        estado: cliente.estado,
                        conta: conta.numero,
                        saldo: conta.saldo,
                        limite: conta.limite,
                        gerente: conta.gerente
                    };
                } catch (err) {
                    console.error(`Falha ao buscar cliente ${conta.cliente}:`, err.message);
                    return null;
                }
            });

            const clientes = (await Promise.all(clientesPromises))
                .filter(c => c !== null)
                .sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR', { sensitivity: 'base' }));

            return res.json(clientes);
        } catch (error) {
            console.error('Erro ao listar clientes do gerente:', error.message);
            return res.status(500).json({ erro: 'Falha ao listar clientes do gerente' });
        }
    }

    if (filtro !== 'adm_relatorio_clientes') {
        return next();
    }

    try {
        const config = configFrom(req);

        const { data: clientes } = await axios.get(`${process.env.CLIENTES_URI}/clientes?filtro=adm_relatorio_clientes`, config);

        const relatorioPromises = clientes.map(async (cliente) => {
            try {
                const conta = await buscarContaPorCliente(cliente.cpf, config);
                return await comporClienteComConta(cliente, conta, config);
            } catch (err) {
                console.error(`Falha ao compor dados do cliente ${cliente.cpf}:`, err.message);
                return null;
            }
        });
        const relatorioCompleto = (await Promise.all(relatorioPromises)).filter(c => c !== null);

        return res.json(relatorioCompleto);

    } catch (error) {
        console.error("Erro na composition de Relatório de Clientes:", error.message);
        return res.status(500).json({ erro: "Falha ao gerar relatório de clientes" });
    }
});


// Rota: Alteração de Perfil do Cliente (Composição de Cliente + Conta + Gerente)
app.get('/clientes/:cpf/perfil', autorizar('CLIENTE'), async (req, res) => {
    const { cpf } = req.params;

    try {
        const config = configFrom(req);
        const { data: cliente } = await axios.get(`${process.env.CLIENTES_URI}/clientes/${cpf}`, config);
        const conta = await buscarContaPorCliente(cpf, config);
        const clienteComposto = await comporClienteComConta(cliente, conta, config);

        return res.json(clienteComposto);
    } catch (error) {
        console.error(`Erro na composition do perfil do cliente ${cpf}:`, error.message);
        return res.status(500).json({ erro: 'Falha ao gerar perfil do cliente' });
    }
});

// Rota: Detalhe de Cliente (Composição de Cliente + Conta + Gerente)
app.get('/clientes/:cpf', autorizar('CLIENTE', 'GERENTE', 'ADMINISTRADOR'), async (req, res) => {
    const { cpf } = req.params;

    try {
        const config = configFrom(req);
        const { data: cliente } = await axios.get(`${process.env.CLIENTES_URI}/clientes/${cpf}`, config);
        const conta = await buscarContaPorCliente(cpf, config);
        const clienteComposto = await comporClienteComConta(cliente, conta, config);

        return res.json(clienteComposto);
    } catch (error) {
        const status = error.response?.status ?? 500;
        console.error(`Erro na composition do cliente ${cpf}:`, error.message);
        return res.status(status).json({ erro: 'Falha ao consultar cliente' });
    }
});

// Rota: Consultar todos os clientes de um gerente (Composição de Gerente + Clientes + Conta)
app.get('/gerentes/:cpf/clientes', autorizar('GERENTE'), async (req, res) => {
    const { cpf } = req.params;

    try {
        const config = configFrom(req);

        const { data: contas } = await axios.get(`${process.env.CONTAS_URI}/contas/gerente/${cpf}`, config);

        const clientesPromises = contas.map(async (conta) => {
            try {
                const { data: cliente } = await axios.get(`${process.env.CLIENTES_URI}/clientes/${conta.cliente}`, config);
                
                return {
                    cpf: cliente.cpf,
                    nome: cliente.nome,
                    cidade: cliente.cidade,
                    estado: cliente.estado,
                    saldo: conta.saldo,
                    limite: conta.limite
                };
            } catch (err) {
                console.error(`Falha ao buscar dados do cliente ${conta.cliente}:`, err.message);
                return null;
            }
        });

        const clientesCompostos = (await Promise.all(clientesPromises))
            .filter(c => c !== null)
            .sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR', { sensitivity: 'base' }));

        return res.json(clientesCompostos);
    } catch (error) {
        console.error(`Erro ao listar clientes do gerente ${cpf}:`, error.message);
        return res.status(500).json({ erro: 'Falha ao listar clientes do gerente' });
    }
});

app.use((req, res, next) => {
    const path = req.path;
    if (path === '/gerentes' || path.startsWith('/gerentes/')) {
        // Exceção 1: GET /gerentes/:cpf/clientes -> Permitido para GERENTE (exigido na rota específica)
        if (req.method === 'GET' && /^\/gerentes\/[^/]+\/clientes\/?$/.test(path)) {
            return next();
        }
        // Exceção 2: GET /gerentes com ?numero=dashboard ou ?filtro=dashboard -> Permitido para GERENTE e ADMINISTRADOR
        if (req.method === 'GET' && path === '/gerentes' && isDashboardRequest(req)) {
            return next();
        }
        // Exceção 3: GET /gerentes com ?cpf=... -> Permitido para obter nome do gerente
        if (req.method === 'GET' && path === '/gerentes' && req.query.cpf) {
            return next();
        }
        // Para qualquer outra rota /gerentes, exige ADMINISTRADOR
        return autorizar('ADMINISTRADOR')(req, res, next);
    }
    next();
});

app.post('/gerentes', express.json(), async (req, res) => {
    const gerente = req.body;
    const config = configFrom(req);

    try {
        try {
            await axios.get(`${process.env.GERENTES_URI}/gerentes/${gerente.cpf}`, config);
            return res.status(409).json({ erro: 'CPF já cadastrado' });
        } catch (error) {
            if (error.response?.status !== 404) {
                throw error;
            }
        }

        const payloadGerente = {
            ...gerente,
            telefone: gerente.telefone ?? ''
        };

        const { data: novoGerente } = await axios.post(`${process.env.GERENTES_URI}/gerentes`, payloadGerente, config);

        await axios.post(`${process.env.AUTH_URI}/usuarios`, {
            cpf: gerente.cpf,
            tipo: 'GERENTE',
            login: gerente.email,
            nome: gerente.nome,
            senha: gerente.senha
        }, { timeout: 10000 });

        await transferirClienteParaNovoGerente(novoGerente.cpf, config);

        return res.status(201).json(novoGerente);
    } catch (error) {
        const status = error.response?.status ?? 500;
        return res.status(status).json(error.response?.data ?? { erro: 'Falha ao inserir gerente' });
    }
});

// Rota: Dashboard do Gerente/Admin (Composição de Gerente + Contas/Saldos)
// Rota: Gerentes (Lista Simples) OU Dashboard (Composição se numero === 'dashboard')
app.get('/gerentes', async (req, res, next) => {
    try {
        const config = configFrom(req);

        // 1. Condicional: Se NÃO for solicitado o dashboard, deixa o proxy (http-proxy-middleware) lidar com isso para repassar query params etc.
        if (!isDashboardRequest(req)) {
            return next();
        }

        // 2. Busca comum para o Dashboard
        const { data: gerentes } = await axios.get(`${process.env.GERENTES_URI}/gerentes`, config);

        // 3. Fluxo do Dashboard: Se chegou aqui, é porque numero === 'dashboard'
        const dashboardPromises = gerentes.map(async (gerente) => {
            try {
                const { data: contas } = await axios.get(`${process.env.CONTAS_URI}/contas/gerente/${gerente.cpf}`, config);

                let saldo_positivo = 0;
                let saldo_negativo = 0;

                contas.forEach(conta => {
                    if (conta.saldo >= 0) {
                        saldo_positivo += conta.saldo;
                    } else {
                        saldo_negativo += conta.saldo;
                    }
                });

                return {
                    gerente: gerente,
                    clientes: contas,
                    saldo_positivo: saldo_positivo,
                    saldo_negativo: saldo_negativo
                };
            } catch (err) {
                console.error(`Falha ao compor contas do gerente ${gerente.cpf}:`, err.message);
                return { gerente: gerente, clientes: [], saldo_positivo: 0, saldo_negativo: 0 };
            }
        });

        let dashboardComposto = await Promise.all(dashboardPromises);

        // Ordenar por saldo
        dashboardComposto.sort((a, b) => b.saldo_positivo - a.saldo_positivo);

        return res.json(dashboardComposto);

    } catch (error) {
        console.error("Erro no processamento de Gerentes:", error.message);
        return res.status(500).json({ erro: "Falha ao processar requisição" });
    }
});


// Rota: Consultar os 3 melhores clientes por saldo (Composição de Gerente + Clientes + Conta)
app.get('/contas/top3', autorizar('GERENTE'), async (req, res) => {
    try {
        const config = configFrom(req);

        const { data: top3Contas } = await axios.get(`${process.env.CONTAS_URI}/contas/top3`, config);

        const clientesPromises = top3Contas.map(async (conta) => {
            try {
                const { data: cliente } = await axios.get(`${process.env.CLIENTES_URI}/clientes/${conta.cliente}`, config);
                
                return {
                    cpf: cliente.cpf,
                    nome: cliente.nome,
                    cidade: cliente.cidade,
                    estado: cliente.estado,
                    saldo: conta.saldo
                };
            } catch (err) {
                console.error(`Falha ao buscar dados do cliente ${conta.cliente}:`, err.message);
                return null;
            }
        });

        const top3Compostos = (await Promise.all(clientesPromises)).filter(c => c !== null);

        return res.json(top3Compostos);
    } catch (error) {
        console.error('Erro ao listar top 3 clientes:', error.message);
        return res.status(500).json({ erro: 'Falha ao listar top 3 clientes' });
    }
});


app.post('/login', express.json(), async (req, res) => {
    try {
        const { login, senha } = req.body;
        const response = await axios.post(`${process.env.AUTH_URI}/login`, {
            login,
            senha
        });

        const usuario = response.data;
        const token = jwt.sign(
            { login: usuario.login, tipo: usuario.tipo, cpf: usuario.cpf },
            process.env.SECRET,
            { expiresIn: '1h' }
        );

        return res.status(200).json({
            access_token: token,
            token_type: "bearer",
            tipo: usuario.tipo,
            usuario: {
                nome: usuario.nome,
                cpf: usuario.cpf,
                email: usuario.login
            }
        });
    } catch (error) {
        if (error.response) {
            const status = error.response.status === 400 ? 401 : error.response.status;
            return res.status(status).json(error.response.data);
        } else {
            console.error("Erro no login do Gateway:", error.message);
            return res.status(500).json({ erro: "Erro interno no servidor" });
        }
    }
});

app.post('/logout', (req, res) => {
    const token = req.headers['authorization']?.split(' ')[1];
    if (token) {
        invalidatedTokens.add(token);
    }
    return res.status(200).json({ email: req.user?.login });
});

// Rota: Reboot - reseta todos os microsserviços para o estado inicial (sem autenticação)
app.get('/reboot', async (req, res) => {
    console.log('[REBOOT] Iniciando reboot de todos os microsserviços...');

    const services = [
        { name: 'MS-Saga',    url: `${process.env.SAGA_URI}/reboot` },
        { name: 'MS-Conta',    url: `${process.env.CONTAS_URI}/contas/reboot` },
        { name: 'MS-Cliente',  url: `${process.env.CLIENTES_URI}/clientes/reboot` },
        { name: 'MS-Gerente',  url: `${process.env.GERENTES_URI}/gerentes/reboot` },
        { name: 'MS-Auth',     url: `${process.env.AUTH_URI}/reboot` },
    ];

    const results = [];
    for (const svc of services) {
        try {
            console.log(`[REBOOT] Chamando ${svc.name}: ${svc.url}`);
            await axios.get(svc.url, { timeout: 30000 });
            results.push({ service: svc.name, status: 'OK' });
            console.log(`[REBOOT] ${svc.name} concluído com sucesso.`);
        } catch (err) {
            const status = err.response?.status ?? 'ERRO';
            const message = err.response?.data ?? err.message;
            results.push({ service: svc.name, status, message });
            console.error(`[REBOOT] Falha em ${svc.name}:`, message);
        }
    }

    const allOk = results.every(r => r.status === 'OK');
    console.log('[REBOOT] Finalizado.', allOk ? 'Todos os serviços OK.' : 'Alguns serviços falharam.');
    return res.status(allOk ? 200 : 207).json({ reboot: allOk ? 'OK' : 'PARCIAL', results });
});

// Rota: Autocadastro de cliente (público — sem autenticação)
app.post('/clientes', express.json(), async (req, res) => {
    try {
        const response = await axios.post(
            `${process.env.CLIENTES_URI}/clientes`,
            req.body,
            { headers: { 'Content-Type': 'application/json' }, timeout: 10000 }
        );
        return res.status(response.status).json(response.data);
    } catch (error) {
        if (error.response) {
            // Preserva 400 (dados inválidos) e 409 (CPF duplicado) vindos do MS-Cliente
            return res.status(error.response.status).json(error.response.data);
        }
        console.error('[POST /clientes] Erro no autocadastro:', error.message);
        return res.status(500).json({ erro: 'Falha ao realizar autocadastro' });
    }
});

app.use('/gerentes', createProxyMiddleware({
    target: process.env.GERENTES_URI,
    changeOrigin: true,
    pathRewrite: withProxyPrefix('/gerentes'),
}));

app.use('/clientes', createProxyMiddleware({
    target: process.env.CLIENTES_URI,
    changeOrigin: true,
    pathRewrite: withProxyPrefix('/clientes'),
}));

app.use('/contas', createProxyMiddleware({
    target: process.env.CONTAS_URI,
    changeOrigin: true,
    pathRewrite: withProxyPrefix('/contas'),
}));

app.listen(PORT, () => {
    console.log(`API Gateway rondando na porta: ${PORT}`);
});
