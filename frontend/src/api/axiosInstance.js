import axios from 'axios'

// instancia de Axios con configuracion base
const axiosInstance = axios.create({
    baseURL: 'http://localhost:8080/api',   // el proxy de Vite redirige esto al backend
    headers: {
        'Content-Type': 'application/json',
    },
})

axiosInstance.interceptors.request.use(
    (config) => {
        // leemos el token del store de Zustand
        // importamos directo (no como hook) porque estamos fuera de un componente
        const accessToken = localStorage.getItem('accessToken')
        // Nota importante: en prod el accessToken va en memoria, no localStorage

        if(accessToken){
            config.headers['Authorization'] = `Bearer ${accessToken}`
        }
        return config
    },
    (error) => Promise.reject(error)
)

// flag para refreshToken
let isRefreshing = false

// cola de request que fallaron mientras se estaba renovando el token
let failedQueue = []

const processQueue = (error, token = null) => {
    // procesa todas las request que estaban esperando el nuevo token
    failedQueue.forEach(promise => {
        if(error) {
            promise.reject(error)
        }else{
            promise.resolve(token)
        }
    })
    failedQueue = []
}

axiosInstance.interceptors.response.use(
    (response) => response,

    async (error) => {
        const originalRequest = error.config

        // Si es 401 y no es el endpoint de refresh ni de login
        // y no se reintento antes
        if(
            error.response?.status === 401 &&
            !originalRequest._retry &&
            !originalRequest.url.includes('/auth/')
        ) {
            if(isRefreshing){
                // Si ya hay un refresh en curso, esta request espera en la cola
                return new Promise((resolve, reject) => {
                    failedQueue.push({resolve, reject})
                }).then(token => {
                    originalRequest.headers['Authorization'] = `Bearer ${token}`
                    return axiosInstance(originalRequest)
                })
            }

            originalRequest._retry = true // marcamos que ya intentamos renovar
            isRefreshing = true

            try{
                const refreshToken = localStorage.getItem('refreshToken')

                if(!refreshToken){
                    // no hay refreshToken -> logout forzado
                    handleLogout()
                    return Promise.reject(error)
                }

                // pedimos un nuevo accessToken
                const response = await axios.post('/api/v1/auth/refresh', {
                    refreshToken,
                })
                const { accessToken } = response.data
                localStorage.setItem('accessToken', accessToken)

                originalRequest.headers['Authorization'] = `Bearer ${accessToken}`

                processQueue(null, accessToken)

                return axiosInstance(originalRequest)

            }catch(refreshError){
                // el refresh fallo -> logout forzado
                processQueue(refreshError, null)
                handleLogout()
                return Promise.reject(refreshError)
            }finally{
                isRefreshing = false
            }
        }

        return Promise.reject(error)
    }
)

const handleLogout = () => {
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    window.location.href = '/login' // redirige al login
}

export default axiosInstance