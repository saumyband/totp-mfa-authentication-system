import axios from "axios";

const AUTH_URL = import.meta.env.VITE_AUTH_SERVICE_URL;

export const setupMfa = (email) => {
  return axios.get(
    `${AUTH_URL}/mfa/setup/${email}`
  );
}