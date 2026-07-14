import axios from "axios";

const USER_URL = import.meta.env.VITE_USER_SERVICE_URL;

export const registerUser = (request) => {
    return axios.post(
        `${USER_URL}/register`,
        request
    );
}