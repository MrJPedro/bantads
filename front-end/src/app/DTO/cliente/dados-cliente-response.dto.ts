export interface DadosClienteResponse {
    id?: number
    cpf: string
    nome: string
    telefone: string
    email: string
    endereco?: string | null
    cep?: string | null
    CEP?: string | null
    cidade?: string | null
    estado?: string | null
    salario: number
    conta?: string | null
    saldo?: number | string | null
    limite?: number | null
    gerente?: string | null
    gerente_cpf?: string | null
    gerenteCpf?: string | null
    gerente_nome?: string | null
    gerente_email?: string | null
    motivoRejeicao?: string | null
    dataRejeicao?: string | null
}
