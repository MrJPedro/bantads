const jsonServer = require("json-server");
const path = require("path");

const server = jsonServer.create();
const router = jsonServer.router(path.join(__dirname, "db.json"));
const middlewares = jsonServer.defaults({ noCors: false });

server.use(middlewares);
server.use(jsonServer.bodyParser);


// gera token fake

function fakeToken(user) {
  return Buffer.from(JSON.stringify({ cpf: user.cpf, tipo: user.tipo })).toString("base64");
}

function decodeToken(req) {
  const auth = req.headers.authorization;
  if (!auth || !auth.startsWith("Bearer ")) return null;
  try {
    return JSON.parse(Buffer.from(auth.split(" ")[1], "base64").toString());
  } catch {
    return null;
  }
}

function requireAuth(req, res) {
  const user = decodeToken(req);
  if (!user) {
    res.status(401).json({ erro: "O usuário não está logado" });
    return null;
  }
  return user;
}

function requireRole(res, user, ...roles) {
  if (!roles.includes(user.tipo)) {
    res.status(403).json({ erro: "O usuário não tem permissão para efetuar esta operação" });
    return false;
  }
  return true;
}

function requireAccountOwner(req, res, db, user, conta) {
  if (user.tipo === "CLIENTE" && conta.cliente !== user.cpf) {
    res.status(403).json({ erro: "O usuário não tem permissão para efetuar esta operação" });
    return false;
  }
  return true;
}


// POST /login

server.post("/login", (req, res) => {
  const { login, senha } = req.body;
  const db = router.db;
  const user = db.get("usuarios").find({ login, senha }).value();

  if (!user) {
    return res.status(401).json({ erro: "Usuário/Senha incorretos" });
  }

  let extra = {};
  if (user.tipo === "CLIENTE") {
    const cliente = db.get("clientes").find({ cpf: user.cpf }).value();
    const conta = db.get("contas").find({ cliente: user.cpf }).value();
    extra = { cliente, conta };
  }

  return res.status(200).json({
    cpf: user.cpf,
    nome: user.nome,
    email: user.email,
    tipo: user.tipo,
    token: fakeToken(user),
    ...extra,
  });
});


// POST /logout

server.post("/logout", (req, res) => {
  const user = requireAuth(req, res);
  if (!user) return;

  const dbUser = router.db.get("usuarios").find({ cpf: user.cpf }).value();
  return res.status(200).json({
    cpf: user.cpf,
    nome: dbUser ? dbUser.nome : "",
    email: dbUser ? dbUser.email : "",
    tipo: user.tipo,
  });
});


// GET /reboot - reinicia o banco (reload do db.json)

server.get("/reboot", (_req, res) => {
  const freshData = JSON.parse(
    require("fs").readFileSync(path.join(__dirname, "db.json"), "utf-8")
  );
  router.db.setState(freshData);
  return res.status(200).json({ mensagem: "Banco de dados reiniciado com sucesso" });
});


// POST /clientes  - autocadastro

server.post("/clientes", (req, res) => {
  const db = router.db;
  const body = req.body;

  const existe = db.get("clientes").find({ cpf: body.cpf }).value();
  if (existe) {
    return res.status(409).json({ erro: "Cliente já cadastrado ou aguardando aprovação, CPF duplicado" });
  }

  const gerentes = db.get("gerentes").filter({ tipo: "GERENTE" }).value();
  let gerenteMenosClientes = gerentes[0];
  let minClientes = Infinity;
  for (const g of gerentes) {
    const count = db.get("clientes").filter({ gerente_cpf: g.cpf, status: "APROVADO" }).value().length;
    if (count < minClientes) {
      minClientes = count;
      gerenteMenosClientes = g;
    }
  }

  const novoCliente = {
    cpf: body.cpf,
    nome: body.nome,
    email: body.email,
    telefone: body.telefone,
    salario: body.salario,
    endereco: body.endereco,
    CEP: body.CEP,
    cidade: body.cidade,
    estado: body.estado,
    status: "PENDENTE",
    gerente_cpf: gerenteMenosClientes ? gerenteMenosClientes.cpf : null,
  };

  db.get("clientes").push(novoCliente).write();

  db.get("usuarios")
    .push({
      login: body.email,
      senha: "tads",
      cpf: body.cpf,
      nome: body.nome,
      email: body.email,
      tipo: "CLIENTE",
    })
    .write();

  return res.status(201).json(novoCliente);
});


// GET /clientes  - com filtros

server.get("/clientes", (req, res) => {
  const db = router.db;
  const filtro = req.query.filtro;
  const user = requireAuth(req, res);
  if (!user) return;

  if (filtro === "para_aprovar") {
    //if (!requireRole(res, user, "GERENTE")) return;
    let pendentes = db.get("clientes").filter({ status: "PENDENTE" }).value();
    pendentes = pendentes.filter((c) => c.gerente_cpf === user.cpf);
    const result = pendentes.map((c) => ({
      cpf: c.cpf,
      nome: c.nome,
      email: c.email,
      salario: c.salario,
      endereco: c.endereco,
      cidade: c.cidade,
      estado: c.estado,
    }));
    return res.status(200).json(result);
  }

  if (filtro === "adm_relatorio_clientes") {
    if (!requireRole(res, user, "ADMINISTRADOR")) return;
    const clientes = db.get("clientes").filter({ status: "APROVADO" }).value();
    const result = clientes.map((c) => {
      const conta = db.get("contas").find({ cliente: c.cpf }).value() || {};
      const gerente = db.get("gerentes").find({ cpf: c.gerente_cpf }).value() || {};
      return {
        cpf: c.cpf,
        nome: c.nome,
        telefone: c.telefone,
        email: c.email,
        endereco: c.endereco,
        cidade: c.cidade,
        estado: c.estado,
        salario: c.salario,
        conta: conta.numero || null,
        saldo: conta.saldo != null ? String(conta.saldo) : null,
        limite: conta.limite || null,
        gerente: gerente.cpf || null,
        gerente_nome: gerente.nome || null,
        gerente_email: gerente.email || null,
      };
    });
    return res.status(200).json(result);
  }

  if (filtro === "melhores_clientes") {
    //if (!requireRole(res, user, "GERENTE")) return;
    let contas = db.get("contas").value();
    contas = contas.filter((ct) => ct.gerente === user.cpf);
    contas.sort((a, b) => b.saldo - a.saldo);
    const top3 = contas.slice(0, 3);
    const result = top3.map((ct) => {
      const c = db.get("clientes").find({ cpf: ct.cliente }).value() || {};
      return {
        cpf: c.cpf,
        nome: c.nome,
        email: c.email,
        telefone: c.telefone,
        endereco: c.endereco,
        cidade: c.cidade,
        estado: c.estado,
        conta: ct.numero,
        saldo: ct.saldo,
        limite: ct.limite,
      };
    });
    return res.status(200).json(result);
  }

  //if (!requireRole(res, user, "GERENTE")) return;
  let contas = db.get("contas").value();
  contas = contas.filter((ct) => ct.gerente === user.cpf);
  const result = contas.map((ct) => {
    const c = db.get("clientes").find({ cpf: ct.cliente }).value() || {};
    return {
      cpf: c.cpf,
      nome: c.nome,
      email: c.email,
      telefone: c.telefone,
      endereco: c.endereco,
      cidade: c.cidade,
      estado: c.estado,
      conta: ct.numero,
      saldo: ct.saldo,
      limite: ct.limite,
    };
  });
  return res.status(200).json(result);
});


// GET /clientes/:cpf

server.get("/clientes/:cpf", (req, res) => {
  const user = requireAuth(req, res);
  if (!user) return;

  const db = router.db;
  const c = db.get("clientes").find({ cpf: req.params.cpf }).value();
  if (!c) return res.status(404).json({ erro: "Usuário não encontrado" });

  const conta = db.get("contas").find({ cliente: c.cpf }).value() || {};
  const gerente = db.get("gerentes").find({ cpf: c.gerente_cpf }).value() || {};

  return res.status(200).json({
    cpf: c.cpf,
    nome: c.nome,
    telefone: c.telefone,
    email: c.email,
    endereco: c.endereco,
    cidade: c.cidade,
    estado: c.estado,
    salario: c.salario,
    conta: conta.numero || null,
    saldo: conta.saldo != null ? String(conta.saldo) : null,
    limite: conta.limite || null,
    gerente: gerente.cpf || null,
    gerente_nome: gerente.nome || null,
    gerente_email: gerente.email || null,
  });
});


// PUT /clientes/:cpf

server.put("/clientes/:cpf", (req, res) => {
  const user = requireAuth(req, res);
  if (!user) return;

  const db = router.db;
  const c = db.get("clientes").find({ cpf: req.params.cpf }).value();
  if (!c) return res.status(404).json({ erro: "Cliente não encontrado" });

  const campos = ["nome", "email", "salario", "endereco", "CEP", "cidade", "estado"];
  const updates = {};
  for (const campo of campos) {
    if (req.body[campo] !== undefined) updates[campo] = req.body[campo];
  }
  db.get("clientes").find({ cpf: req.params.cpf }).assign(updates).write();

  return res.status(200).json({ mensagem: "Perfil do cliente alterado com sucesso" });
});


// POST /clientes/:cpf/aprovar

server.post("/clientes/:cpf/aprovar", (req, res) => {
  const user = requireAuth(req, res);
  if (!user) return;
  //if (!requireRole(res, user, "GERENTE")) return;

  const db = router.db;
  const c = db.get("clientes").find({ cpf: req.params.cpf }).value();
  if (!c) return res.status(404).json({ erro: "Cliente não encontrado" });

  db.get("clientes").find({ cpf: req.params.cpf }).assign({ status: "APROVADO" }).write();

  const numero = String(Math.floor(1000 + Math.random() * 9000));
  const limite = c.salario >= 2000 ? c.salario * 0.5 : 0;
  const novaConta = {
    numero,
    cliente: c.cpf,
    saldo: 0,
    limite,
    gerente: c.gerente_cpf,
    criacao: new Date().toISOString(),
  };
  db.get("contas").push(novaConta).write();

  return res.status(200).json({
    cliente: c.cpf,
    numero,
    saldo: 0,
    limite,
    gerente: c.gerente_cpf,
    criacao: novaConta.criacao,
  });
});


// POST /clientes/:cpf/rejeitar

server.post("/clientes/:cpf/rejeitar", (req, res) => {
  const user = requireAuth(req, res);
  if (!user) return;
  //if (!requireRole(res, user, "GERENTE")) return;

  const db = router.db;
  const c = db.get("clientes").find({ cpf: req.params.cpf }).value();
  if (!c) return res.status(404).json({ erro: "Cliente não encontrado" });

  db.get("clientes").find({ cpf: req.params.cpf }).assign({ status: "REJEITADO" }).write();

  return res.status(200).json({ mensagem: "Cliente rejeitado com sucesso", motivo: req.body.motivo || "" });
});


// GET /contas/:numero/saldo

server.get("/contas/:numero/saldo", (req, res) => {
  const user = requireAuth(req, res);
  if (!user) return;

  const db = router.db;
  const conta = db.get("contas").find({ numero: req.params.numero }).value();
  if (!conta) return res.status(404).json({ erro: "Conta não encontrada" });
  if (!requireAccountOwner(req, res, db, user, conta)) return;

  return res.status(200).json({
    cliente: conta.cliente,
    conta: conta.numero,
    saldo: conta.saldo,
  });
});


// POST /contas/:numero/depositar

server.post("/contas/:numero/depositar", (req, res) => {
  const user = requireAuth(req, res);
  if (!user) return;

  const db = router.db;
  const conta = db.get("contas").find({ numero: req.params.numero });
  const contaVal = conta.value();
  if (!contaVal) return res.status(404).json({ erro: "Conta não encontrada" });
  if (!requireAccountOwner(req, res, db, user, contaVal)) return;

  const valor = req.body.valor;
  if (!valor || valor <= 0) return res.status(400).json({ erro: "Valor deve ser maior que zero" });
  const novoSaldo = contaVal.saldo + valor;
  conta.assign({ saldo: novoSaldo }).write();

  const data = new Date().toISOString();
  db.get("movimentacoes")
    .push({ conta: contaVal.numero, data, tipo: "depósito", origem: null, destino: contaVal.numero, valor })
    .write();

  return res.status(200).json({ conta: contaVal.numero, data, saldo: novoSaldo });
});


// POST /contas/:numero/sacar

server.post("/contas/:numero/sacar", (req, res) => {
  const user = requireAuth(req, res);
  if (!user) return;

  const db = router.db;
  const conta = db.get("contas").find({ numero: req.params.numero });
  const contaVal = conta.value();
  if (!contaVal) return res.status(404).json({ erro: "Conta não encontrada" });
  if (!requireAccountOwner(req, res, db, user, contaVal)) return;

  const valor = req.body.valor;
  if (!valor || valor <= 0) return res.status(400).json({ erro: "Valor deve ser maior que zero" });
  const limiteDisponivel = contaVal.saldo + contaVal.limite;
  if (valor > limiteDisponivel) {
    return res.status(400).json({ erro: "Saldo insuficiente" });
  }

  const novoSaldo = contaVal.saldo - valor;
  conta.assign({ saldo: novoSaldo }).write();

  const data = new Date().toISOString();
  db.get("movimentacoes")
    .push({ conta: contaVal.numero, data, tipo: "saque", origem: contaVal.numero, destino: null, valor })
    .write();

  return res.status(200).json({ conta: contaVal.numero, data, saldo: novoSaldo });
});


// POST /contas/:numero/transferir

server.post("/contas/:numero/transferir", (req, res) => {
  const user = requireAuth(req, res);
  if (!user) return;

  const db = router.db;
  const contaOrigem = db.get("contas").find({ numero: req.params.numero });
  const origemVal = contaOrigem.value();
  if (!origemVal) return res.status(404).json({ erro: "Conta origem não encontrada" });
  if (!requireAccountOwner(req, res, db, user, origemVal)) return;

  const contaDestino = db.get("contas").find({ numero: req.body.destino });
  const destinoVal = contaDestino.value();
  if (!destinoVal) return res.status(404).json({ erro: "Conta destino não encontrada" });

  const valor = req.body.valor;
  if (!valor || valor <= 0) return res.status(400).json({ erro: "Valor deve ser maior que zero" });
  const limiteDisponivel = origemVal.saldo + origemVal.limite;
  if (valor > limiteDisponivel) {
    return res.status(400).json({ erro: "Saldo insuficiente" });
  }

  const novoSaldoOrigem = origemVal.saldo - valor;
  const novoSaldoDestino = destinoVal.saldo + valor;
  contaOrigem.assign({ saldo: novoSaldoOrigem }).write();
  contaDestino.assign({ saldo: novoSaldoDestino }).write();

  const data = new Date().toISOString();
  db.get("movimentacoes")
    .push({ conta: origemVal.numero, data, tipo: "transferência", origem: origemVal.numero, destino: destinoVal.numero, valor })
    .write();
  db.get("movimentacoes")
    .push({ conta: destinoVal.numero, data, tipo: "transferência", origem: origemVal.numero, destino: destinoVal.numero, valor })
    .write();

  return res.status(200).json({
    conta: origemVal.numero,
    data,
    destino: destinoVal.numero,
    saldo: novoSaldoOrigem,
    valor,
  });
});


// GET /contas/:numero/extrato

server.get("/contas/:numero/extrato", (req, res) => {
  const user = requireAuth(req, res);
  if (!user) return;

  const db = router.db;
  const conta = db.get("contas").find({ numero: req.params.numero }).value();
  if (!conta) return res.status(404).json({ erro: "Conta não encontrada" });
  if (!requireAccountOwner(req, res, db, user, conta)) return;

  const movimentacoes = db.get("movimentacoes").filter({ conta: conta.numero }).value();

  return res.status(200).json({
    conta: conta.numero,
    saldo: conta.saldo,
    movimentacoes: movimentacoes.map((m) => ({
      data: m.data,
      tipo: m.tipo,
      origem: m.origem,
      destino: m.destino,
      valor: m.valor,
    })),
  });
});


// GET /gerentes

server.get("/gerentes", (req, res) => {
  const user = requireAuth(req, res);
  if (!user) return;

  const db = router.db;
  const filtro = req.query.numero;

  if (filtro === "dashboard") {
    const gerentes = db.get("gerentes").filter({ tipo: "GERENTE" }).value();
    const result = gerentes.map((g) => {
      const contas = db.get("contas").filter({ gerente: g.cpf }).value();
      const saldo_positivo = contas.filter((c) => c.saldo >= 0).reduce((sum, c) => sum + c.saldo, 0);
      const saldo_negativo = contas.filter((c) => c.saldo < 0).reduce((sum, c) => sum + c.saldo, 0);
      return {
        gerente: { cpf: g.cpf, nome: g.nome, email: g.email, tipo: g.tipo },
        clientes: contas.map((c) => ({
          cliente: c.cliente,
          numero: c.numero,
          saldo: c.saldo,
          limite: c.limite,
          gerente: c.gerente,
          criacao: c.criacao,
        })),
        saldo_positivo,
        saldo_negativo,
      };
    });
    return res.status(200).json(result);
  }

  const gerentes = db.get("gerentes").value();
  return res.status(200).json(
    gerentes.map((g) => ({ cpf: g.cpf, nome: g.nome, email: g.email, tipo: g.tipo }))
  );
});


// GET /gerentes/:cpf

server.get("/gerentes/:cpf", (req, res) => {
  const user = requireAuth(req, res);
  if (!user) return;
  if (!requireRole(res, user, "ADMINISTRADOR")) return;

  const db = router.db;
  const g = db.get("gerentes").find({ cpf: req.params.cpf }).value();
  if (!g) return res.status(404).json({ erro: "Gerente não encontrado" });
  return res.status(200).json({ cpf: g.cpf, nome: g.nome, email: g.email, tipo: g.tipo });
});


// POST /gerentes  - inserção

server.post("/gerentes", (req, res) => {
  const user = requireAuth(req, res);
  if (!user) return;
  if (!requireRole(res, user, "ADMINISTRADOR")) return;

  const db = router.db;
  const body = req.body;

  const existe = db.get("gerentes").find({ cpf: body.cpf }).value();
  if (existe) return res.status(409).json({ erro: "Gerente já cadastrado" });

  const novo = {
    cpf: body.cpf,
    nome: body.nome,
    email: body.email,
    tipo: body.tipo || "GERENTE",
    senha: body.senha || "tads",
  };
  db.get("gerentes").push(novo).write();

  db.get("usuarios")
    .push({ login: body.email, senha: body.senha || "tads", cpf: body.cpf, nome: body.nome, email: body.email, tipo: novo.tipo })
    .write();

  return res.status(200).json({ cpf: novo.cpf, nome: novo.nome, email: novo.email, tipo: novo.tipo });
});


// PUT /gerentes/:cpf  - atualização

server.put("/gerentes/:cpf", (req, res) => {
  const user = requireAuth(req, res);
  if (!user) return;
  if (!requireRole(res, user, "ADMINISTRADOR")) return;

  const db = router.db;
  const g = db.get("gerentes").find({ cpf: req.params.cpf });
  if (!g.value()) return res.status(404).json({ erro: "Gerente não encontrado" });

  const updates = {};
  if (req.body.nome) updates.nome = req.body.nome;
  if (req.body.email) updates.email = req.body.email;
  if (req.body.senha) updates.senha = req.body.senha;
  g.assign(updates).write();

  const updated = g.value();
  return res.status(200).json({ cpf: updated.cpf, nome: updated.nome, email: updated.email, tipo: updated.tipo });
});


// DELETE /gerentes/:cpf

server.delete("/gerentes/:cpf", (req, res) => {
  const user = requireAuth(req, res);
  if (!user) return;
  if (!requireRole(res, user, "ADMINISTRADOR")) return;

  const db = router.db;
  const g = db.get("gerentes").find({ cpf: req.params.cpf }).value();
  if (!g) return res.status(404).json({ erro: "Gerente não encontrado" });

  db.get("gerentes").remove({ cpf: req.params.cpf }).write();
  db.get("usuarios").remove({ cpf: req.params.cpf }).write();

  const clientesOrfaos = db.get("clientes").filter({ gerente_cpf: req.params.cpf }).value();
  const gerentesRestantes = db.get("gerentes").filter({ tipo: "GERENTE" }).value();
  if (gerentesRestantes.length > 0 && clientesOrfaos.length > 0) {
    let i = 0;
    for (const c of clientesOrfaos) {
      const novoGerente = gerentesRestantes[i % gerentesRestantes.length];
      db.get("clientes").find({ cpf: c.cpf }).assign({ gerente_cpf: novoGerente.cpf }).write();
      db.get("contas").find({ cliente: c.cpf }).assign({ gerente: novoGerente.cpf }).write();
      i++;
    }
  }

  return res.status(200).json({ cpf: g.cpf, nome: g.nome, email: g.email, tipo: g.tipo });
});


// Inicialização

const PORT = process.env.PORT || 3001;
server.listen(PORT, () => {
  console.log(`\n  BANTADS Mock API rodando em http://localhost:${PORT}\n`);
  console.log("  Endpoints disponíveis:");
  console.log("  ──────────────────────────────────────────");
  console.log("  POST   /login");
  console.log("  POST   /logout");
  console.log("  GET    /reboot");
  console.log("  GET    /clientes                 ?filtro=para_aprovar|adm_relatorio_clientes|melhores_clientes");
  console.log("  POST   /clientes                 (autocadastro)");
  console.log("  GET    /clientes/:cpf");
  console.log("  PUT    /clientes/:cpf");
  console.log("  POST   /clientes/:cpf/aprovar");
  console.log("  POST   /clientes/:cpf/rejeitar");
  console.log("  GET    /contas/:numero/saldo");
  console.log("  POST   /contas/:numero/depositar");
  console.log("  POST   /contas/:numero/sacar");
  console.log("  POST   /contas/:numero/transferir");
  console.log("  GET    /contas/:numero/extrato");
  console.log("  GET    /gerentes                 ?numero=dashboard");
  console.log("  POST   /gerentes");
  console.log("  GET    /gerentes/:cpf");
  console.log("  PUT    /gerentes/:cpf");
  console.log("  DELETE /gerentes/:cpf");
  console.log("  ──────────────────────────────────────────\n");
});
