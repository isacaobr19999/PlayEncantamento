# PlayEncantamento 🌟

> **PlayEncantamento** é um plugin de encantamentos personalizados de nível profissional (*Eternal Edition*) desenvolvido para servidores **PaperMC 1.21.x**. Criado sob medida por **_Nube**, este plugin oferece uma experiência RPG completa com itens customizados, sistema de evolução por experiência, sockets, gemas e controle administrativo avançado.

---

## 🚀 Principais Funcionalidades

- **Encantamentos Personalizados**:
  - *Lifesteal* & *Vampirism*: Roubo de vida e bônus de dano noturno com partículas e ganho de XP.
  - *Explosive Pickaxe*: Mineração em área (3x3) com quebra inteligente de blocos.
  - *Telekinesis*: Recolhimento automático de drops direto para o inventário.
  - *Soulbound*: Proteção de itens essenciais na morte do jogador.
  - *Divine Aura*, *Berserker*, *Frostbite*, *Hardened*, *Flight* e *Mending II*.
- **Overrides de Encantamentos Baunilha**:
  - Suporte completo para **Eficiência 10**, **Fortuna 10**, **Afiação 10**, **Proteção 10** e **Inquebrável 10**.
- **Sistema Eternal (Evolução)**:
  - Os encantamentos acumulam experiência através do uso contínuo (combate e mineração) e sobem de nível organicamente, exibindo barras de progresso diretamente na lore dos itens.
- **Sockets & Gemas**:
  - Inserção de gemas customizadas (**Rubi**, **Safira**, **Esmeralda**, **Topaz**) em itens com soquetes para ampliação de atributos estatísticos.
- **Economia e Integrações**:
  - Integração nativa com **Vault** (Economia), **PlaceholderAPI** (Placeholders customizados), **WorldGuard** e **GriefPrevention** (Proteção de áreas e blocos).

---

## 📦 Instalação e Requisitos

1. **Requisitos**:
   - Servidor rodando **PaperMC 1.21.1+** (ou forks compatíveis).
   - **Java 21** instalado no host.
   - (Opcional) *Vault* + Plugin de Economia e *PlaceholderAPI* para funcionalidade completa.
2. **Instalação**:
   - Baixe a versão mais recente do JAR na aba [Releases](../../releases).
   - Coloque o arquivo `PlayEncantamento-1.0-SNAPSHOT.jar` na pasta `plugins/` do seu servidor.
   - Inicie ou reinicie o servidor para gerar os arquivos de configuração (`config.yml` e `lang.yml`).

---

## 🛠️ Comandos Principais

- `/ce menu` - Abre o painel GUI principal de encantamentos e gerenciamento.
- `/ce give <jogador> <encantamento> [nível]` - Concede um livro de encantamento personalizado.
- `/ce reload` - Recarrega as configurações e arquivos de linguagem do plugin.

---

## 📄 Licença

Este projeto é distribuído sob a licença MIT. Sinta-se à vontade para contribuir, relatar problemas ou sugerir melhorias!

---
*Autor: **_Nube***

## Permissões

- `customenchants.menu`: permite abrir o menu; padrão: todos os jogadores.
- `customenchants.reload`: permite recarregar configurações; padrão: operadores.
- `customenchants.give`: permite conceder encantamentos; padrão: operadores.
- `customenchants.items`: permite criar orbes, gemas, pó e ferramentas; padrão: operadores.
- `customenchants.enchant`: permite aplicar encantamentos diretamente; padrão: operadores.
- `customenchants.admin`: grupo administrativo que inclui as permissões acima.

## Comandos administrativos

Além de `/ce menu`, o plugin oferece `/ce reload`, `/ce give <jogador> <encantamento> [nível]`, `/ce orb <encantamento> [chance]`, `/ce dust [percentual]`, `/ce whitescroll`, `/ce blackscroll`, `/ce gem <tipo>` e `/ce socket`. Os comandos administrativos exigem a permissão correspondente.

## Build e validação

O projeto usa Java 21. Para compilar de forma reproduzível, execute `./mvnw -B clean verify`. O mesmo comando é executado automaticamente pelo GitHub Actions em pushes e pull requests. O plugin deve ser validado em um servidor Paper 1.21.x com versões compatíveis das dependências externas.

## Limitações conhecidas

O repositório ainda não contém testes automatizados de integração com um servidor Paper. Antes de usar em produção, recomenda-se testar inventários, duplo clique, inventário cheio, economia, reload, voo, mineração em área e as integrações de proteção em um servidor de homologação.
