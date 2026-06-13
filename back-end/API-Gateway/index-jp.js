//require('dotenv-safe').config();
const jwt = require('jsonwebtoken');
var http = require('http');
var express = require('express');
const httpProxy = require('express-http-proxy');
const app = express();
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var logger = require('morgan');
const helmet = require('helmet');

app.use(bodyParser.urlencoded({extended: false}));

app.use(bodyParser.json())

// proxies para microssrviços
// > ...

function verifyJWT(req, res, next) {
    const token = req.headers['Authorization'];

    if (!token) return res.status(401).json({auth: false, message: 'Token não fornecido'});

    jwt.verify(token, process.env.SECRET, function (err, decoded) {
        if (err) return res.status(500).json({auth: false, message: 'Falha ao autenticar o token.'});

        req.userId = decoded.id
        next();
    });
}

app.post('/login', urlencodedParser, (req, res, next) => {

    try {
        const {login, senha} = req.body
        const response = await fetch(process.env.AUTH_URI + "/login",
            {
            method: "post",
            body: JSON.stringify({
                login: login,
                senha: senha
            })
        });

        switch(response.status){
            case 200:
                const tipoUsuario = response.body.tipo
                const nomeUsuario = response.body.nome
                const cpfUsuario = response.body.cpf
                const token = jwt.sign({login: login, tipoUsuario: tipoUsuario}, process.env.SECRET, {expiresIn: "1h"})

                res.status(200).json({
                    access_token: token,
                    token_type: "bearer",
                    tipo: tipoUsuario,
                    usuario: {
                        nome: nomeUsuario,
                        cpf: cpfUsuario,
                        email: login
                    }
                })
                break

            default:
                res = response
        }
        return res
    } catch (err) {
        console.log(err)
        res.status(500).json({mensagem: "Erro interno"})
    }
});

app.post('/logout', urlencodedParser, (req, res) => {
    res.json({auth: false, token: null});
});

app.use(logger('dev'));
app.use(helmet());
app.use(express.json())
app.use(express.urlencoded({extended: false}))
app.use(cookieParser())

var server = http.createServer(app)
server.listen(3000)
