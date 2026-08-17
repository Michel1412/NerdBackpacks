# NerdBackpacks

Mochilas por níveis para **Minecraft 1.20.1 (Forge)**, com inventário no estilo vanilla, modelo vestível e suporte opcional a Curios.

---

## Visão geral

O **NerdBackpacks** adiciona cinco mochilas aprimoráveis que ganham **+18 slots** a cada nível (duas fileiras a mais). Dá para usar na mão, colocar no mundo, vestir no peitoral vanilla ou equipar pelo Curios (se o mod estiver instalado).

O conteúdo fica salvo no item (e no bloco quando colocada), então os upgrades **mantêm** o inventário.

| Nível | Item | Slots |
|------|------|-------|
| I | Mochila Nível I | 27 |
| II | Mochila Nível II | 45 |
| III | Mochila Nível III | 63 |
| IV | Mochila Nível IV | 81 |
| V | Mochila Nível V | 99 |

---

## Requisitos

| | |
|---|---|
| **Minecraft** | 1.20.1 |
| **Loader** | Forge 47+ |
| **Dependências obrigatórias** | Apenas o Forge |

### Integrações opcionais

| Mod | O que adiciona |
|-----|----------------|
| **Curios** | Equipar nos slots `back` / `chest`; renderização nas costas |
| **JEI** | Mostra craft e upgrades das mochilas |
| **Jade** | Mostra o conteúdo da mochila colocada (segure Shift) |
| **Mouse Tweaks** | Arrastar itens na GUI (o scroll do Mouse Tweaks fica desativado para o scroll da mochila funcionar) |

---

## Como usar

### Craft

1. Craft do **Saco** (`saco`) com linha e couro.
2. Craft da **Mochila Nível I** e depois os upgrades na mesa de craft (mochila anterior + materiais).
3. Ao upar, o inventário da mochila é **preservado** (NBT).

### Interface

- Clique direito com a mochila na mão para abrir.
- Mochilas maiores (IV–V) têm **barra de rolagem** e scroll do mouse (até 7 fileiras visíveis).
- **Clique do meio** na área da mochila para **organizar** (A→Z, stacks cheios primeiro).
- Não é possível colocar mochila dentro de mochila.

### Colocar e pegar

- **Shift + clique direito** com a mochila para **colocar** no mundo.
- **Clique direito** na mochila colocada para abrir.
- **Shift + clique direito** na mochila colocada para **pegar** (o inventário continua no item).

### Vestir e abrir equipada

- Equipe no slot de **peitoral** vanilla **ou** no Curios (**Back** / **Chest**).
- Só é permitido **uma** mochila vestida por vez (peitoral **ou** Curios — não os dois / não dois slots Curios).
- Tecla **B** (padrão) abre a mochila equipada — remapeável em *Controles → Nerd Backpacks*.
- Clique direito no ar com a mão vazia também abre a mochila vestida.

Mochilas no chão usam **hitbox alinhada ao modelo** (não o bloco inteiro). Equipadas, renderizam nas costas do player.

---

## Idiomas

- Inglês (`en_us`)
- Português do Brasil (`pt_br`)

---

## Versão

Lançamento atual: **1.0.0** — veja [`changelog/1.0.0.md`](changelog/1.0.0.md) (em inglês, padrão CurseForge).

---

## Licença

Consulte o campo de licença em `mods.toml` (`All Rights Reserved`, salvo indicação contrária do autor).
