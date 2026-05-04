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
                    <label class="form-label">Contrasena</label>
                    <input v-model="password" type="password" placeholder="Ingrese su contrasena"/>
                </div>

                <button class="btn-loggin" @click=" ">Iniciar Sesion</button>
                <a href="#" class="forgotlink">Perdiste tu contrasena</a>

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