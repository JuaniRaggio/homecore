<template>
    <div class="login-bg">
        <div class="login-container">
            <!-- Icono y titulo -->

            <div class="login-header">
                <div class="icon-app">
                    <img src="@/assets/icons/homecore-icon.png" alt="HomeCore Icon" class="icon-image">
                </div>
                <h1 class="login-title">HomeCore</h1>
            </div>
           
            <!-- Formulario de login -->
             <div class="login-card">
                <div class="form-group">
                    <label class="form-label">Email </label>
                    <input v-model="email" type="email" placeholder="Ingrese su email"/>
                </div>

                <div class="form-group">
                    <label class="form-label">Contraseña</label>
                    <input v-model="password" type="password" placeholder="Ingrese su contrasena"/>
                </div>

                <button class="btn-loggin" @click=" ">Iniciar Sesion</button>
                <a href="#" class="forgotlink">Perdiste tu contraseña</a>

             </div>
             <!--Boton crear cuenta-->
             <button class="btn-register" @click="">Crear Cuenta</button>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const email = ref('');
const password = ref('');

function handleLogin() {
//Aca deberiamos poner la logica de autenticacion, por ahora solo redirigimos a home
    router.push('/home');
}

function handleRegister() {
    router.push('/register');
}
</script>


<style scoped>
.login-bg {
  min-height: 100vh;
  background: radial-gradient(ellipse at center, #1a1a3a 0%, #0d0d1a 70%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  width: 100%;
  max-width: 340px;
  padding: 24px 16px;
}

.login-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.app-icon {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background-color: #3b7fd4;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 20px rgba(59, 127, 212, 0.4);
}

.icon-img {
  width: 40px;
  height: 40px;
  filter: brightness(0) invert(1);
}

.app-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: 0.5px;
}

.login-card {
  width: 100%;
  background-color: #2a2a4a;
  border-radius: 14px;
  padding: 24px 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-label {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-primary);
}

.form-input {
  width: 100%;
  padding: 10px 14px;
  border-radius: 8px;
  border: 1px solid var(--border);
  background-color: #3a3a5e;
  color: var(--text-primary);
  font-size: 14px;
  font-family: 'Inter', sans-serif;
  outline: none;
  transition: border-color 0.2s;
}

.form-input::placeholder {
  color: var(--text-muted);
}

.form-input:focus {
  border-color: var(--accent);
}

.btn-login {
  width: 100%;
  padding: 12px;
  background-color: #1a1a2e;
  color: var(--text-primary);
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 600;
  font-family: 'Inter', sans-serif;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-login:hover {
  background-color: #0f0f20;
}

.forgot-link {
  font-size: 13px;
  color: var(--text-muted);
  text-align: center;
  text-decoration: underline;
  cursor: pointer;
  transition: color 0.2s;
}

.forgot-link:hover {
  color: var(--text-primary);
}

.btn-register {
  width: 100%;
  padding: 13px;
  background-color: var(--accent);
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  font-family: 'Inter', sans-serif;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-register:hover {
  background-color: var(--accent-hover);
}
</style>
