import { ItemExtratoResponse } from "../gerente/item-extrato-response"

export interface ExtratoResponse {
    conta: string
    saldo: number
    movimentacoes: ItemExtratoResponse[]
    
}
