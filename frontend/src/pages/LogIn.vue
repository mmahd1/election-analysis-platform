<template>
    <div>
        <NavBarComponent/>

        <div class="login-container">
            <div class="login-card">

                <h2>Inloggen</h2>

                <div v-if="loginError" class="error-banner">
                    {{ loginError }}
                </div>

                <input
                    v-model="email"
                    type="text"
                    placeholder="Email"
                    :class="{ error: emailError }"
                />
                <p v-if="emailError" class="error-text">
                    Vul een geldig emailadres in
                </p>

                <input
                    v-model="password"
                    type="password"
                    placeholder="Wachtwoord"
                    :class="{ error: passwordError }"
                />
                <p v-if="passwordError" class="error-text">
                    Wachtwoord is verplicht
                </p>

                <button @click="handleLogin">
                    Inloggen
                </button>

                <p class="auth-switch">
                    Nog geen account?
                    <RouterLink to="/register">Registreer hier</RouterLink>
                </p>

            </div>
        </div>
    </div>
</template>

<script lang="ts">
import {defineComponent} from 'vue'
import NavBarComponent from '../components/NavBarComponent.vue'
import {loginUser} from '../api/authApi'

export default defineComponent({

    components: {
        NavBarComponent
    },

    data() {
        return {
            email: '',
            password: '',

            emailError: false,
            passwordError: false,

            loginError: ''
        }
    },

    methods: {

        validateForm() {
            this.emailError = false
            this.passwordError = false
            this.loginError = ''

            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

            let valid = true

            if (!this.email || !emailRegex.test(this.email)) {
                this.emailError = true
                valid = false
            }

            if (!this.password) {
                this.passwordError = true
                valid = false
            }

            return valid
        },

        async handleLogin() {

            // reset server error bij nieuwe poging
            this.loginError = ''

            if (!this.validateForm()) {
                return
            }

            try {

                const token = await loginUser(
                    this.email,
                    this.password
                )

                localStorage.setItem('token', token)

                this.$router.push('/profile')

            } catch (error: any) {

                if (error?.status === 401) {
                    this.loginError = 'Onjuiste email of wachtwoord'
                } else {
                    this.loginError = 'Er ging iets mis. Probeer het opnieuw.'
                }

                console.error(error)
            }
        }
    }
})
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #eef2ff, #f8fafc);
  padding: 20px;
  box-sizing: border-box;
}

.login-card {
  width: 420px;
  max-width: 100%;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(12px);
  padding: 40px;
  border-radius: 18px;
  box-shadow: 0 25px 60px rgba(0, 0, 0, 0.12);
  text-align: center;
  border: 1px solid rgba(255, 255, 255, 0.4);
}

h2 {
  margin-bottom: 25px;
  font-size: 26px;
  color: #111827;
}

.error-banner {
  background: #fef2f2;
  border: 1px solid #fecaca;
  color: #b91c1c;
  padding: 12px 14px;
  border-radius: 10px;
  font-size: 14px;
  margin-bottom: 15px;
  text-align: left;
}

input {
  width: 100%;
  padding: 14px 16px;
  margin: 10px 0;
  border-radius: 10px;
  border: 1px solid #e5e7eb;
  font-size: 15px;
  outline: none;
  transition: all 0.2s ease;
  box-sizing: border-box;
}

input:focus {
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.2);
}

button {
  width: 100%;
  padding: 14px;
  margin-top: 15px;
  background: linear-gradient(135deg, #6366f1, #3b82f6);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

button:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 25px rgba(59, 130, 246, 0.3);
}

button:active {
  transform: scale(0.98);
}

.error {
  border: 1px solid #ef4444 !important;
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.15);
}

.error-text {
  text-align: left;
  font-size: 12px;
  color: #ef4444;
  margin: -6px 0 10px 0;
}

@media (max-width: 768px) {
  .login-card {
    padding: 28px;
    border-radius: 14px;
  }

  h2 {
    font-size: 22px;
  }

  button {
    font-size: 15px;
    padding: 12px;
  }
}

@media (max-width: 420px) {
  .login-card {
    padding: 20px;
  }

  input {
    padding: 12px;
    font-size: 14px;
  }
}

.auth-switch {
    margin-top: 18px;
    font-size: 14px;
    color: #4b5563;
}

.auth-switch a {
    color: #2563eb;
    font-weight: 600;
    text-decoration: none;
}
</style>
