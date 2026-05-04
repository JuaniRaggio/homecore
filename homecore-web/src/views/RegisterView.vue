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
  background: radial-gradient(ellipse at center, #2c2f3e 0%, #1a1c26 50%, #0e0d13 100%);
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
  background-color: #3b6fd4;
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
  font-size: 22px;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: 0.5px;
}

.HomeCore-group {
  width: 100%;
  background-color: rgba(90, 95, 115, 0.45);
  border-radius: 14px;
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
  font-size: 13px;
  color: #c8d0e8;
  text-decoration: underline;
  cursor: pointer;
}

.back-link:hover {
  color: #ffffff;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.form-label {
  font-size: 13px;
  font-weight: 500;
  color: #d0d3db;
}

.form-group input {
  background-color: rgba(200, 210, 230, 0.18);
  border: 1px solid rgba(200, 210, 230, 0.25);
  border-radius: 8px;
  padding: 9px 12px;
  font-size: 14px;
  color: #ffffff;
  outline: none;
  transition: border-color 0.2s;
}

.form-group input::placeholder {
  color: rgba(200, 210, 230, 0.55);
}

.form-group input:focus {
  border-color: var(--accent, #6f78da);
}

.btn-creteAccount {
  margin-top: 6px;
  width: 100%;
  padding: 12px;
  background-color: #3b4a8a;
  color: #ffffff;
  font-size: 15px;
  font-weight: 600;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-creteAccount:hover {
  background-color: #4f5fa8;
}
</style>