# Changelog

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
