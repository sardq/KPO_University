import './App.css';

export default function Header({ pageTitle, logoSrc }) {
  return (
    <header className="App-header d-flex justify-content-between align-items-center px-4">
      <div className="header-container">
        <div className="d-flex align-items-center">
          <img src={logoSrc} className="App-logo" alt="logo" />
          <h1 className="App-title ml-3">{pageTitle}</h1>
        </div>
      </div>
    </header>
  );
}