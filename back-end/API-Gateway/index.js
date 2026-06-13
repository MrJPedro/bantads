const express = require('express');
const cors = require('cors');
const { createProxyMiddleware } = require('http-proxy-middleware');
const axios = require('axios');

const app = express();
const PORT = process.env.PORT;

function configFrom(req) {
    return {
        headers: { Authorization: req.headers['authorization'] },
        timeout: 5000
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

app.use(cors({
    origin: process.env.FRONT, 
    methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS', 'PATCH'],
    allowedHeaders: ['*'], 
    credentials: true     
}));


// Rota: Relatório do Administrador (Composição de Clientes + Conta + Gerente)
app.get('/clientes', async (req, res, next) => {
    const { filtro } = req.query;

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
app.get('/clientes/:cpf/perfil', async (req, res) => {
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
app.get('/clientes/:cpf', async (req, res) => {
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

// Rota: Dashboard do Gerente/Admin (Composição de Gerente + Contas/Saldos)
app.get('/gerentes', async (req, res, next) => {
    const { numero } = req.query;
    if (numero !== 'dashboard') {
        return next();
    }

    try {
        const config = configFrom(req);

        const { data: gerentes } = await axios.get(`${process.env.GERENTES_URI}/gerentes`, config);

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
        console.error("Erro na composition do Dashboard de Gerentes:", error.message);
        return res.status(500).json({ erro: "Falha ao gerar dashboard" });
    }
});


// Rota: Consultar todos os clientes de um gerente (Composição de Gerente + Clientes + Conta)
app.get('/gerentes/:cpf/clientes', async (req, res) => {
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


// Rota: Consultar os 3 melhores clientes por saldo (Composição de Gerente + Clientes + Conta)
app.get('/contas/top3', async (req, res) => {
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


app.use('/login', createProxyMiddleware({
    target: process.env.AUTH_URI,
    changeOrigin: true,
}));

app.use('/gerentes', createProxyMiddleware({
    target: process.env.GERENTES_URI,
    changeOrigin: true,
}));

app.use('/clientes', createProxyMiddleware({
    target: process.env.CLIENTES_URI,
    changeOrigin: true,
}));

app.use('/contas', createProxyMiddleware({
    target: process.env.CONTAS_URI,
    changeOrigin: true,
}));

app.listen(PORT, () => {
    console.log(`API Gateway rondando na porta: ${PORT}`);
});
