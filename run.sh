#!/bin/bash

COMPOSE_FILE="compose.yml"

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' 

if ! command -v docker &> /dev/null; then
    echo -e "${RED}Erro: Docker não está instalado ou não está no PATH.${NC}"
    exit 1
fi

case "$1" in
    start)
        echo -e "${GREEN}Iniciando o ecossistema de microsserviços...${NC}"
        # A flag --build garante que imagens locais sejam recriadas se houver alteração
        docker compose -f $COMPOSE_FILE up -d --build
        echo -e "${GREEN}Containers em execução.${NC}"
        ;;
    stop)
        echo -e "${YELLOW}Encerrando os serviços...${NC}"
        docker compose -f $COMPOSE_FILE down
        echo -e "${GREEN}Ambiente finalizado com sucesso.${NC}"
        ;;
    restart)
        echo -e "${YELLOW}Reiniciando o ambiente...${NC}"
        docker compose -f $COMPOSE_FILE down
        docker compose -f $COMPOSE_FILE up -d --build
        ;;
    logs)
        # Opcional: passar o nome de um serviço específico após "logs" (ex: ./manager.sh logs rabbitmq)
        if [ -z "$2" ]; then
            docker compose -f $COMPOSE_FILE logs -f
        else
            docker compose -f $COMPOSE_FILE logs -f "$2"
        fi
        ;;
    ps)
        docker compose -f $COMPOSE_FILE ps
        ;;
    *)
        echo "Uso: $0 {start|stop|restart|logs [serviço]|ps}"
        exit 1
        ;;
esac