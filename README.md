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
   - Coloque o arquivo `PlayEncantamento-10.2.5.jar` na pasta `plugins/` do seu servidor.
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

## Permissões e perfis de acesso

O plugin separa o acesso ao menu público das operações administrativas. As permissões administrativas devem ser configuradas pelo sistema de permissões do servidor, como LuckPerms.

| Permissão | Padrão | Acesso concedido |
|---|---:|---|
| `customenchants.menu` | Todos | Abre o menu de encantamentos. |
| `customenchants.reload` | Operadores | Recarrega configurações e idioma. |
| `customenchants.give` | Operadores | Concede encantamentos a jogadores. |
| `customenchants.items` | Operadores | Cria orbes, gemas, pó, scrolls e ferramentas. |
| `customenchants.enchant` | Operadores | Aplica encantamentos diretamente em itens. |
| `customenchants.admin` | Operadores | Permissão agrupadora com todas as permissões administrativas acima. |

### Jogador normal

O jogador normal pode usar `customenchants.menu` e utilizar legitimamente os encantamentos presentes nos itens que possuir. Ele não deve receber `customenchants.admin`, `customenchants.give`, `customenchants.items`, `customenchants.reload` ou `customenchants.enchant`.

### VIP

O grupo VIP herda o acesso do jogador normal. Esta versão não concede poderes administrativos automáticos ao VIP e não deve receber `customenchants.admin` nem `customenchants.items`, pois essas permissões permitem gerar itens e podem afetar a economia do servidor.

Para benefícios VIP futuros, utilize permissões separadas, como `customenchants.vip`, `customenchants.vip.xp`, `customenchants.vip.gems` e `customenchants.vip.effects`. Essas permissões só produzirão efeitos quando a respectiva funcionalidade estiver implementada.

### Moderador

Um moderador pode receber apenas as permissões necessárias para suas funções. Uma configuração recomendada é `customenchants.give` e, caso necessário, `customenchants.items`. Não é obrigatório conceder `customenchants.reload` ou `customenchants.enchant`.

### Administrador

O administrador pode receber `customenchants.admin`, que inclui `reload`, `give`, `items` e `enchant`. Como essas permissões podem gerar itens e alterar a economia, o acesso deve ficar restrito à equipe de confiança.

## Comandos administrativos

Além de `/ce menu`, o plugin oferece `/ce reload`, `/ce give <jogador> <encantamento> [nível]`, `/ce orb <encantamento> [chance]`, `/ce dust [percentual]`, `/ce whitescroll`, `/ce blackscroll`, `/ce gem <tipo>` e `/ce socket`. Cada subcomando verifica sua permissão correspondente. O tab completion também filtra as sugestões para não exibir operações que o jogador não pode executar.

### Exemplos de LuckPerms

```text
/lp group default permission set customenchants.menu true
/lp group vip parent add default
/lp group vip permission set customenchants.vip true
/lp group moderador permission set customenchants.give true
/lp group moderador permission set customenchants.items true
/lp group admin permission set customenchants.admin true
```

Não conceda `customenchants.admin` a VIPs ou jogadores comuns. Para retirar explicitamente uma permissão de um grupo, use `false` no LuckPerms quando houver herança conflitante.

## Build e validação

A versão atual é **10.2.5** e usa Java 21. Para compilar de forma reproduzível, execute `./mvnw -B clean verify`.
 O mesmo comando é executado automaticamente pelo GitHub Actions em pushes e pull requests. O comando `/ce` é registrado pela API de comandos do Paper plugin e não deve ser adicionado a `commands` no `paper-plugin.yml`. O plugin deve ser validado em um servidor Paper 1.21.11 com PlaceholderAPI e Vault instalados quando os hooks correspondentes forem utilizados com versões compatíveis das dependências externas.

## Limitações conhecidas

O repositório ainda não contém testes automatizados de integração com um servidor Paper. Antes de usar em produção, recomenda-se testar inventários, duplo clique, inventário cheio, economia, reload, voo, mineração em área e as integrações de proteção em um servidor de homologação.
