<template>
    <div>
        <NavBarComponent />

        <div class="register-container">
            <div class="register-card">

                <h2>Registreren</h2>

                <div v-if="registerError" class="error-banner">
                    {{ registerError }}
                </div>

                <div v-if="registerSuccess" class="success-banner">
                    {{ registerSuccess }}
                </div>

                <input
                    v-model="firstName"
                    type="text"
                    placeholder="Voornaam"
                    :class="{ error: firstNameError }"
                />
                <p v-if="firstNameError" class="error-text">
                    Voornaam is verplicht
                </p>

                <input
                    v-model="lastName"
                    type="text"
                    placeholder="Achternaam"
                    :class="{ error: lastNameError }"
                />
                <p v-if="lastNameError" class="error-text">
                    Achternaam is verplicht
                </p>

                <input
                    v-model="email"
                    type="email"
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
                    Wachtwoord moet minimaal 6 tekens zijn
                </p>

                <button @click="handleRegister">
                    Registreren
                </button>

                <p class="auth-switch">
                    Al een account?
                    <RouterLink to="/logIn">Log hier in</RouterLink>
                </p>

            </div>
        </div>
    </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import NavBarComponent from '../components/NavBarComponent.vue'
import { registerUser } from '../api/authApi'

export default defineComponent({

    components: {
        NavBarComponent
    },

    data() {
        return {
            firstName: '',
            lastName: '',
            email: '',
            password: '',

            firstNameError: false,
            lastNameError: false,
            emailError: false,
            passwordError: false,

            registerError: '',
            registerSuccess: ''
        }
    },

    methods: {

        validateForm() {
            this.firstNameError = false
            this.lastNameError = false
            this.emailError = false
            this.passwordError = false
            this.registerError = ''
            this.registerSuccess = ''

            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
            let valid = true

            if (!this.firstName.trim()) {
                this.firstNameError = true
                valid = false
            }

            if (!this.lastName.trim()) {
                this.lastNameError = true
                valid = false
            }

            if (!this.email || !emailRegex.test(this.email)) {
                this.emailError = true
                valid = false
            }

            if (!this.password || this.password.length < 6) {
                this.passwordError = true
                valid = false
            }

            return valid
        },

        async handleRegister() {
            if (!this.validateForm()) {
                return
            }

            try {
                await registerUser({
                    firstName: this.firstName.trim(),
                    lastName: this.lastName.trim(),
                    email: this.email.trim(),
                    password: this.password
                })

                this.registerSuccess = 'Account is aangemaakt. Je kunt nu inloggen.'

                setTimeout(() => {
                    this.$router.push('/logIn')
                }, 900)
            } catch (error: any) {
                if (error?.status === 409) {
                    this.registerError = 'Dit emailadres is al in gebruik'
                } else {
                    this.registerError = 'Registreren is mislukt. Probeer het opnieuw.'
                }

                console.error(error)
            }
        }
    }
})
</script>

<style scoped>
.register-container {
    min-height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    background: linear-gradient(135deg, #eef2ff, #f8fafc);
    padding: 20px;
}

.register-card {
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

.success-banner {
    background: #ecfdf5;
    border: 1px solid #bbf7d0;
    color: #047857;
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
