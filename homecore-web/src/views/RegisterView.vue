<template>
  <div class="HomeCore-bg">
    <div class="HomeCore-conteiner">

      <div class="HomeCore-header">
        <div class="icon-app">
          <img
            src="@/assets/homecore-icono.svg" alt="HomeCore Icon" class="icon-image"/>
         </div>
          <h1 class="HomeCore-title">HomeCore</h1>
       </div>

      <div class="HomeCore-group">
        <div class="card-top-row">
          <h2 class="HomeCore-title">Registrarse</h2>
          <a class="back-link" @click.prevent="router.back()">← Volver</a>
         </div>

        <div class="form-group">
          <label class="form-label">Nombre</label>
          <input v-model="name" type="text" placeholder="Ingrese su nombre" />
        </div>

        <div class="form-group">
          <label class="form-label">Email</label>
          <input v-model="email" type="email" placeholder="Ingrese su email" />
        </div>

        <div class="form-group">
           <label class="form-label">Contraseña</label>
          <input v-model="password" type="password" placeholder="Ingrese su contraseña" />
        </div>

        <div class="form-group">
          <label class="form-label">Confirme su contraseña</label>
          <input
             v-model="confirmPassword"
             type="password"
            placeholder="Confirme su contraseña"
          />
        </div>

         <button class="btn-creteAccount" @click="handleRegister">Crear Cuenta</button>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const name = ref('');
const email = ref('');
const password = ref('');
const confirmPassword = ref('');

function handleRegister() {
  //Conectar con la API

  if (password.value !== confirmPassword.value) {
    alert('Las contraseñas no coinciden');
    return;
  }

  console.log({
    name: name.value,
    email: email.value,
    password: password.value
  });
}
</script>


<style scoped>
.HomeCore-bg {
  min-height: 100vh;
  background: radial-gradient(ellipse at center, var(--bg-auth-gradient-start) 0%, var(--bg-auth-gradient-end) 70%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.HomeCore-conteiner {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  width: 100%;
  max-width: 380px;
  padding: 24px 16px;
}

.HomeCore-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.icon-app {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  background-color: var(--accent);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.icon-image {
  width: 42px;
  height: 42px;
  object-fit: contain;
}

.HomeCore-title {
  font-size: var(--font-4xl);
  font-weight: 700;
  color: var(--text-on-accent);
  letter-spacing: 0.5px;
}

.HomeCore-group {
  width: 100%;
  background-color: var(--bg-auth-card);
  border-radius: var(--radius-2xl);
  padding: 24px 28px 28px;
  display: flex;
  flex-direction: column;
  gap: 14px;
  backdrop-filter: blur(6px);
}

.card-top-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 4px;
}

.back-link {
  font-size: var(--font-base);
  color: var(--text-secondary);
  text-decoration: underline;
  cursor: pointer;
}

.back-link:hover {
  color: var(--text-on-accent);
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.form-label {
  font-size: var(--font-base);
  font-weight: 500;
  color: var(--text-primary);
}

.form-group input {
  background-color: var(--bg-auth-input);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: 9px 12px;
  font-size: var(--font-md);
  color: var(--text-on-accent);
  outline: none;
  transition: border-color 0.2s;
}

.form-group input::placeholder {
  color: var(--text-muted);
}

.form-group input:focus {
  border-color: var(--accent);
}

.btn-creteAccount {
  margin-top: 6px;
  width: 100%;
  padding: 12px;
  background-color: var(--bg-auth-btn);
  color: var(--text-on-accent);
  font-size: var(--font-lg);
  font-weight: 600;
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-creteAccount:hover {
  background-color: var(--bg-auth-btn-hover);
}
</style>