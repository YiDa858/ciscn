import * as webauthnJson from "@github/webauthn-json";

// Make the call that returns the credentialCreateJson above
const credentialCreateOptions = await fetch(/* ... */).then(resp => resp.json());

// Call WebAuthn ceremony using webauthn-json wrapper
const publicKeyCredential = await webauthnJson.create(credentialCreateOptions);

// Return encoded PublicKeyCredential to server
fetch(/* ... */, { body: JSON.stringify(publicKeyCredential) });