import "./App.css";
import { GoogleAuthProvider, signInWithPopup } from "firebase/auth";
import { auth } from "./config/firebase-config";

function App() {
  const loginWithGoogle = async (e) => {
    const provider = await new GoogleAuthProvider();
    return signInWithPopup(auth, provider)
      .then((result) => {
        const credential = GoogleAuthProvider.credentialFromResult(result);
        const token = credential.idToken;
        const user = result.user;
        console.log(credential);
        console.log(token);
        console.log(user);
      })
      .catch((error) => {
        console.log(error);
      });
  };

  return (
    <div className="App">
      <button onClick={loginWithGoogle}>Log in with Google</button>
    </div>
  );
}

export default App;
