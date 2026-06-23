import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom';
import { registerUser } from '../services/userService';

export const Register = () => {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const navigate = useNavigate();

    const handleRegister = async () => {
        try {
            await registerUser({
                email,
                password,
            });

            navigate("/setup-mfa", {
                state: {
                    email,
                },
            });
        } catch (error) {
            console.log(error);
        }
    }
    return (
        <>
            Register
            <input
                type='email'
                value={email}
                onChange={(e) => setEmail(e.target.value)}
            />
            <input
                type='password'
                value={password}
                onChange={(e) => setPassword(e.target.value)}
            />
            <button
                onClick={handleRegister}
            >
                Register
            </button>
        </>
    )
}
