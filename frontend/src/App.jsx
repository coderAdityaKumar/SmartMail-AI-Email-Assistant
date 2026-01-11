import React, { use, useState } from "react";
import logo from "../public/logo.png";
import axios from "axios";

function App() {
  const [emailContent, setEmailContent] = useState("");
  const [tone, setTone] = useState("none");
  const [generatedEmail, setGeneratedEmail] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    const emailRequest = { emailContent, tone };
    try {
      setLoading(true);
      const reply =await axios.post(
        "http://localhost:8080/api/email/generate",
        emailRequest
      );
      setGeneratedEmail(reply.data);
    } catch (error) {
      setError("Something has happened");
    }finally{
      setLoading(false);
      setError(false);
    }
  }
  return (
    <div className="m-0 p-0 box-border min-h-screen flex flex-col">
      <nav>
        <img src={logo} alt="logo" className="h-25 w-40 md:h-20 md:w-21" />
      </nav>
      <hr className="border-t" />
      <div className="flex items-center justify-center p-5 flex-1 w-full lg:max-w-4xl m-auto">
        <div className="flex-col flex gap-4 w-full">
          <h1 className="font-extrabold tracking-wider text-3xl text-center">
            AI EMAIL GENERATOR
          </h1>
          <textarea
            name=""
            id=""
            value={emailContent}
            placeholder="Paste your email here"
            className="w-full p-2 border border-gray-700 "
            rows={10}
            onChange={function (e) {
              setEmailContent(e.target.value);
            }}
          ></textarea>
          <select
            name=""
            id=""
            className="border border-gray-700 p-2"
            onChange={function (e) {
              setTone(e.target.value);
            }}
          >
            <option value="neutral">Select Tone</option>
            <option value="Polite">Polite</option>
            <option value="Friendly">Friendly</option>
            <option value="Formal">Formal</option>
            <option value="Casual">Casual</option>
            <option value="Neutral">Neutral</option>
          </select>
          <button
            className="border border-gray-700 p-2 bg-cyan-500"
            onClick={handleSubmit}
          >
            {loading?"Generating...":"Generate"}
          </button>
          <div className={generatedEmail ? "flex flex-col gap-2" : "hidden"}>
            <h2 className="font-bold tracking-wider text-2xl">AI Generated Reply : </h2>
            {error?error:<textarea
              name=""
              id=""
              value={generatedEmail}
              rows={10}
              readOnly
              className="w-full p-2 border border-gray-700"
            ></textarea>}
            
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;
