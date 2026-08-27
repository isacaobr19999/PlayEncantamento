# Changelog

## 10.2.4 — 2026-08-27

### Correção de integração

- Declarados PlaceholderAPI e Vault como dependências opcionais de servidor.
- Habilitado `join-classpath` para que as APIs opcionais sejam acessíveis em Paper plugins.
- Definida ordem de carregamento `BEFORE` para as integrações.
- Corrigido o `NoClassDefFoundError` de `me.clip.placeholderapi.expansion.PlaceholderExpansion` observado no servidor.

## 10.2.3 — 2026-08-26

### Permissões e segurança de uso

- Documentada a matriz de acesso para jogadores normais, VIPs, moderadores e administradores.
- Documentadas as permissões e os comandos administrativos.
- Corrigido o tab completion para exibir somente subcomandos permitidos ao executor.
- Registrado que VIPs não devem receber permissões de geração de itens ou administração.

## 10.2.2 — 2026-08-26

### Correção crítica de inicialização

- Substituída a tag inexistente `minecraft:enchantable/vanishing_revealable` pela tag oficial `ENCHANTABLE_VANISHING` do Paper 1.21.11.
- Corrigida a falha `Missing tag` que impedia o carregamento dos datapacks e bloqueava a inicialização do servidor.

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
