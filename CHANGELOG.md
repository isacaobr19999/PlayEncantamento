# Changelog

## 10.2.1 — 2026-08-26

### Correções

- Substituído o evento incompatível `freeze()` pelo evento `compose()` para registrar encantamentos customizados no Paper 1.21.11.
- Atualizada a dependência Paper API para 1.21.11.
- Atualizados os nomes dos atributos Bukkit renomeados na API 1.21.11.
- Corrigido o erro de carregamento que impedia o servidor de reconhecer o plugin.

## 10.2.0 — 2026-08-26

Esta versão reúne melhorias de segurança operacional, validação de comandos, lifecycle e testes automatizados.

### Alterações principais

- Permissões granulares para menu, reload, concessão, criação de itens e aplicação de encantamentos.
- Validação de percentuais e níveis de encantamento.
- Proteção contra perda de itens em inventários cheios.
- Correção do controle de voo para preservar permissões concedidas por outras fontes.
- Recuperação segura de itens Soulbound no respawn.
- Maven Wrapper, GitHub Actions e testes unitários JUnit 5.

## Unreleased

### Corrigido

- Adicionadas permissões granulares para menu, reload, concessão de encantamentos e criação de itens.
- Adicionada validação de chances, percentuais e níveis numéricos nos comandos.
- Itens criados por comandos agora são lançados no mundo quando o inventário está cheio, evitando perda silenciosa.
- Tarefas do plugin e estado do provedor Vault são limpos no desligamento.

### Infraestrutura

- Adicionado Maven Wrapper.
- Adicionado workflow GitHub Actions para validação com Java 21.
- Atualizado o README com comandos, permissões, build e limitações conhecidas.

### Limitações

- A validação completa depende de um servidor Paper 1.21.x e das integrações externas disponíveis em runtime.
- Testes automatizados de integração ainda precisam ser adicionados.
