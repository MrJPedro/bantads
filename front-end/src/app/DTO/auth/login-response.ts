export type TipoUsuario = 'CLIENTE' | 'GERENTE' | 'ADMINISTRADOR'

export interface LoginClienteData {
    id: number
    cpf: string
    nome: string
    email: string
    telefone: string
    salario: number
    endereco: string
    CEP: string
    cidade: string
    estado: string
    status: string
    gerente_cpf: string
}

export interface LoginContaData {
    id: number
    numero: string
    cliente: string
    saldo: number
    limite: number
    gerente: string
    criacao: string
}

export interface LoginResponse {
  access_token: string;
  token_type: string;
  tipo: TipoUsuario;
  usuario: {
    nome: string;
    cpf: string;
    email: string;
  };
}
