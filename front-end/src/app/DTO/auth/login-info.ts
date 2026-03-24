export interface LoginInfo {
    login: string
    senha: string
    // Esse parâmetro serve somente para a implementação
    // realizada pelo João Leal no json-server. Após a apresentação
    // do protótipop, será estudado como implementar autenticação
    // com Tokens JWT, mas ainda assim talvez seja necessário manter
    // esse atributo para o token JWT
    //token: string
}
