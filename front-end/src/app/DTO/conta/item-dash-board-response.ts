import { DadoGerente } from "../gerente/dado-gerente.dto";
import { DadoConta } from "./dado-conta";

export interface ItemDashBoardResponse {
    gerente: DadoGerente
    clientes: DadoConta[]
    saldo_positivo: number
    saldo_negativo: number
}
