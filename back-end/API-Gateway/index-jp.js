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
    const token = req.headers['x-access-token'];

    if (!token) return res.status(401).json({auth: false, message: 'Token não fornecido'});

    jwt.verify(token, process.env.SECRET, function (err, decoded) {
        if (err) return res.status(500).json({auth: false, message: 'Falha ao autenticar o token.'});

        req.userId = decoded.id
        next();
    });
}

app.post('/login', urlencodedParser, (req, res, next) => {
    const response = await fetch(process.env.AUTH_URI, {
        method: "post",
        body: JSON.stringify({
            login: req.body.login,
            senha: req.body.senha
        })
    });
    const responseJson = await response.json();
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
