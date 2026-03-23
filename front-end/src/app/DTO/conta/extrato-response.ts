import { ItemExtratoResponse } from "../gerente/item-extrato-response.dto"

export interface ExtratoResponse {
    conta: string
    saldo: number
    movimentacoes: ItemExtratoResponse[]
    
}
