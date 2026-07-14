import React, { useEffect, useState, } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { setupMfa } from '../services/authService';

export const SetupMfa = () => {
  const [qrCode, setQrCode] = useState("");
  const location = useLocation();
  const navigate = useNavigate();

  const email = location.state?.email;

  const fetchQr =
    async () => {
      const response =
        await setupMfa(email);
      setQrCode(
        response.data.qrCodeBase64
      );
    };


  const handleClick = () => {
    navigate("/activate-mfa", {
      state: {
        email
      },
    })
  }

  useEffect(() => {
    fetchQr();
  }, []);


  return (
    <>
      <div>SetupMfa</div>
      <img
        src={`data:image/png;base64,${qrCode}`}
        alt='QR Code'
      />
      <button
        onClick={handleClick}
      >
        I Have Scanned QR
      </button>
    </>
  );
}
