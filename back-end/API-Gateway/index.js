const express = require('express');
const cors = require('cors');
const { createProxyMiddleware } = require('http-proxy-middleware');
const axios = require('axios');

const app = express();
const PORT = process.env.PORT;

app.use(cors({
    origin: process.env.FRONT, 
    methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS', 'PATCH'],
    allowedHeaders: ['*'], 
    credentials: true     
}));

// Rota: Relatório do Administrador (Composição de Clientes + Conta + Gerente)
app.get('/clientes', async (req, res, next) => {
    // Lembrar de mandar o filtro no front
    const { filtro } = req.query;
    if (filtro !== 'adm_relatorio_clientes') {
        return next();
    }

    try {
        // Repassa o Token de Autenticação para os microsserviços
        const config = { headers: { Authorization: req.headers['authorization'] } };

        const { data: clientes } = await axios.get(`${process.env.CLIENTES_URI}/clientes?filtro=adm_relatorio_clientes`, config);

        const relatorioPromises = clientes.map(async (cliente) => {
            try {
                const { data: conta } = await axios.get(`${process.env.CONTAS_URI}/contas/cliente/${cliente.cpf}`, config);

                const { data: gerente } = await axios.get(`${process.env.GERENTES_URI}/gerentes/${conta.gerente}`, config);

                return {
                    cpf: cliente.cpf,
                    nome: cliente.nome,
                    telefone: cliente.telefone,
                    email: cliente.email,
                    endereco: cliente.endereco,
                    cidade: cliente.cidade,
                    estado: cliente.estado,
                    salario: cliente.salario,
                    conta: conta.numero,
                    saldo: conta.saldo,
                    limite: conta.limite,
                    gerente: gerente.cpf,
                    gerente_nome: gerente.nome,
                    gerente_email: gerente.email
                };
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


// Rota: Dashboard do Gerente/Admin (Composição de Gerente + Contas/Saldos)
app.get('/gerentes', async (req, res, next) => {
    // Acho que era para ser filtro em vez de número, mas na especificação da API tá assim, vai saber
    const { numero } = req.query;
    if (numero !== 'dashboard') {
        return next();
    }

    try {
        const config = { headers: { Authorization: req.headers['authorization'] } };

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