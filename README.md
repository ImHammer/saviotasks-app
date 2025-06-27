# Savio Tasks

## ✨ Visão Geral

O **Savio Tasks** é um aplicativo interno desenvolvido para resolver um problema real de distribuição e controle de tarefas em uma sorveteria. Originalmente, as tarefas eram distribuídas de forma manual e desorganizada, muitas vezes caindo em pessoas erradas ou acumulando. Um sistema web foi criado para melhorar isso, mas sua usabilidade era limitada.

Para resolver isso de forma prática e acessível no dia a dia dos funcionários, nasceu o app nativo **Savio Tasks**, com foco em mobilidade, notificacoes em tempo real, e controle efetivo das tarefas da equipe.

## ⚙️ Funcionalidades

* Login de funcionário e login administrativo
* Visualização de tarefas atribuídas
* Criação e distribuição de tarefas (via conta admin)
* Notificações push em tempo real quando novas tarefas são atribuídas
* Controle de disponibilidade: mostra quem está disponível, de férias ou de folga
* Integração com API central de tarefas

## 🚀 Stack Tecnológico

* **Kotlin** com **Jetpack Compose** (UI nativa Android)
* **Firebase Cloud Messaging** para envio de notificações push
* **REST API** para integração com o sistema central de tarefas
* **Firebase Authentication** para controle de login

## 📅 Roadmap Básico

* [x] Implementar autenticação de usuários com Firebase
* [x] Criar tela de visualização de tarefas por usuário
* [x] Implementar painel administrativo para criação e distribuição de tarefas
* [x] Adicionar notificações push com Firebase Cloud Messaging
* [x] Implementar sistema de status (disponível, férias, folga)
* [ ] Refinar a interface com melhorias de usabilidade
* [ ] Sincronização com o sistema web após sua manutenção
* [ ] Adição de relatórios simples (ex: tarefas concluídas por semana)

## 🖼️ Screenshots

<h4>Tela de login</h4>
<img src="https://github.com/user-attachments/assets/db3e9576-1fec-472c-9bbe-f505bcb8a0e9" width="300" />

<h4>Lista de tarefas</h4>
<img src="https://github.com/user-attachments/assets/ddc7d8d5-4d36-44a4-80d3-b2ba13f396c5" width="300" />

<h4>Notificações Push</h4>
<img src="https://github.com/user-attachments/assets/c64ed623-ff45-4ebb-a7eb-303f998a5975" width="300" />

## 🔗 Considerações de Desenvolvimento

* O app foi desenvolvido para uso interno e privado, com foco em desempenho e simplicidade
* Permite autonomia dos funcionários ao visualizar suas tarefas de forma rápida via smartphone
* O sistema API-first permite expansão futura e integração com outras plataformas
* Futuramente pode ser adaptado para uso em outras empresas com necessidades similares

---

> Desenvolvido por \Danilo como projeto de portfólio, com base em uma necessidade real de uma equipe de trabalho.

---
