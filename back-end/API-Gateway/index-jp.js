require('dotenv-safe').config();
const jwt = require('jsonwebtoken');
var http = require('http');
var express = require('express');
const httpProxy = require('express-http-proxy');
const app = express();
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var logger = require('morgan');
const helmet = require('helmet');

app.use(logger('dev'));
app.use(helmet());
app.use(express.json())
app.use(express.urlencoded({extended: false}))
app.use(cookieParser())

app.use(bodyParser.urlencoded({extended: false}));

app.use(bodyParser.json())

// proxies para microssrviços
// > ...

function verifyJWT(req, res, next) {
    const tokenHeader = req.headers['authorization'];
    const token = tokenHeader?.split(' ')[1]

    if (!token) return res.status(401).json({auth: false, message: 'Token não fornecido'});

    jwt.verify(token, process.env.SECRET, function (err, decoded) {
        if (err) return res.status(400).json({auth: false, message: 'Falha ao autenticar o token.'});

        req.userId = decoded
        next();
    });
}

app.post('/login', async (req, res, next) => {

    try {
        const {login, senha} = req.body
        const response = await fetch(process.env.AUTH_URI + "/login",
            {
            method: "post",
            headers: {'Content-Type':'application/json'},
            body: JSON.stringify({
                login: login,
                senha: senha
            })
        });

        switch(response.status){
            case 200: {
                const data = await response.json()
                const tipoUsuario = data.tipo
                const nomeUsuario = data.nome
                const cpfUsuario =data.cpf
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
            }

            default: {
                const data = await response.json()
                return res.status(response.status).json(response.body)
            }
        }
    } catch (err) {
        console.log(err)
        res.status(500).json({mensagem: "Erro interno"})
    }
});

app.post('/logout', urlencodedParser, (req, res) => {
    res.json({auth: false, token: null});
});

var server = http.createServer(app)
server.listen(3001)
