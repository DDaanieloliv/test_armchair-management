
## **Front-End**

### **Pré-requisitos Front-End**

Antes de começar, certifique-se de ter instalado:

- [Node.js](https://nodejs.org/) (versão 18 ou superior).

- [Angular CLI](https://angular.io/cli) (versão 17 ou superior).

- Um editor de código, como [Visual Studio Code](https://code.visualstudio.com/).


---

### **1. Clone o Repositório**

Se você ainda não clonou o repositório, siga os passos abaixo:

```bash
git clone https://github.com/DDaanieloliv/test_armchair-management.git
cd test_armchair-management/armchair-management-ui
```

---

### **2. Instale as Dependências**

Instale as dependências do projeto usando o npm (Node Package Manager):

```bash
npm install
```


Isso instalará todos os pacotes necessários listados no arquivo `package.json`.

---

### **3. Configure o Ambiente**

Certifique-se de que o ambiente está configurado corretamente. No arquivo `src/environments/environment.ts`, defina a URL da API:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080', // URL da API back-end
};
```

---

### **4. Inicie o Servidor de Desenvolvimento**

Execute o seguinte comando para iniciar o servidor de desenvolvimento:

```bash
npm start
```

Por padrão, o servidor estará disponível em:

```
http://localhost:4200
```

Abra o navegador e acesse o endereço acima para visualizar a aplicação.

---
