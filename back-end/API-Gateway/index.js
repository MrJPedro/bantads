const express = require('express');
const cors = require('cors');
const { createProxyMiddleware } = require('http-proxy-middleware');

const app = express();
const PORT = process.env.PORT;

app.use(cors({
    origin: process.env.FRONT, 
    methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS', 'PATCH'],
    allowedHeaders: ['*'], 
    credentials: true     
}));

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