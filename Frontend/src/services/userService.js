import axios from "axios";

const USER_URL = import.meta.env.VITE_USER_SERVICE_URL;

export const registerUser = (request) => axios.post(
    `${USER_URL}/register`,
    request
);