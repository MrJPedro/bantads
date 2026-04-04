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
    cpf: string
    nome: string
    email: string
    tipo: TipoUsuario
    token: string
    cliente?: LoginClienteData
    conta?: LoginContaData
}
