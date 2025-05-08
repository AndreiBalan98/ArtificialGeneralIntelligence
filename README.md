# 🤖 Smart Secretary – Artificial General Intelligence

Acesta este repository-ul oficial al **echipei LLM**, parte din proiectul mai amplu **Smart Secretary** realizat pentru disciplina *Ingineria Programării* – Facultatea de Informatică.

> 🔍 Acest repo acoperă **doar componenta de chatbot**, dezvoltată de echipa LLM. Alte echipe din proiect se ocupă de front-end și back-end general.

## 👥 Echipa LLM

- Miruna *(Scrum Master)*
- Andrei
- Florea
- Ștefan

---

## 🧠 Arhitectură generală (flux detaliat)

```plaintext
[Chat UI - front-end] 
        ↓
[ChatController.java - back-end Java Spring]
        ↓
      (1) → [RAG API - Python FastAPI]
               ↳ [Chroma DB] ← generat cu create_embeddings.py din PDF-uri
               ↳ Răspuns augmentat cu info relevante
        ↓
      (2) → [LLM - Microsoft API sau Ollama (Mistral)]
               ↳ Primește întrebarea + contextul din RAG
               ↳ Returnează răspunsul final către utilizator
```

> 🔄 ChatController intermediază între UI, RAG și LLM.

---

## 📁 Structura proiectului

```plaintext
ArtificialGeneralIntelligence/
├── front-end/                  # Interfața de utilizator (HTML/JS - ulterior Angular)
├── back-end/                   # Java Spring Boot
│   ├── ChatApplication.java
│   ├── ChatController.java
│   ├── ChatRequest.java
│   └── ChatResponse.java
└── rag/                        # Retrieval-Augmented Generation (Python)
    ├── rag_api.py             # FastAPI server
    ├── create_embeddings.py   # Generează Chroma DB din PDF-uri
    ├── chroma.db/             # Vector store cu embeddings
    ├── pdfuri/                # PDF-uri sursă
    └── start_rag.bat          # Script pornire server API
```

---

## 🚀 Pași pentru rulare completă

### 🔁 1. Clonează repository-ul

```bash
git clone https://github.com/AndreiBalan98/ArtificialGeneralIntelligence.git
cd ArtificialGeneralIntelligence
git checkout -b nume_tău  # ex: git checkout -b andrei
```

---

### ☕️ 2. Back-end (Java Spring Boot)

1. Deschide proiectul în **IntelliJ IDEA**.
2. Dacă apare eroare la SDK:
   - Urmează pașii din IntelliJ pentru a alege un SDK compatibil
   - Alege o versiune maximă de Java 1.5 (ideal 1.4 dacă există deja)
3. Rulează aplicația.
4. Accesează `http://localhost:8081` în browser pentru test.

---

### 🐍 3. Setup Python pentru RAG API

#### a. Activează mediul virtual

> ⚠️ *Execută întâi comanda pentru a permite rularea scripturilor PowerShell:*

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```

Apoi:

```bash
cd rag
python -m venv venv
venv\Scripts\activate  # sau source venv/bin/activate pe Linux/macOS
```

#### b. Instalează pachetele necesare (durează câteva minute)

```bash
pip install fastapi langchain_community langchain_chroma langchain_huggingface \
uvicorn[standard] pdf2image pytesseract pypdf
```

> ⚠️ Asigură-te că instalezi în mediu activat `venv`.

---

### 📦 4. Instalează dependințele externe

#### 🧠 Tesseract OCR

- [Download Tesseract](https://github.com/tesseract-ocr/tesseract/releases/download/5.5.0/tesseract-ocr-w64-setup-5.5.0.20241111.exe)
- Instalează într-un folder ușor accesibil (ex: `S:\Aplicatii\tesseract.exe`)
- În `create_embeddings.py`, **modifică** linia:

```python
pytesseract.pytesseract.tesseract_cmd = r'S:\Aplicatii\tesseract.exe'
```

#### 📄 Poppler

- [Download Poppler](https://github.com/oschwartz10612/poppler-windows/releases)
- Dezarhivează folderul (ex: `C:\poppler`)
- Adaugă calea completă către `C:\poppler\Library\bin` în **Environment Variables → Path**

---

### 📚 5. PDF-uri și crearea embeddings

1. Descarcă PDF-urile de la [Google Drive](https://drive.google.com/drive/folders/1kGZSMCW-wyRKZBo-4R9tl7fGSHeNdNud?usp=sharing)
2. Creează folderul `pdfuri/` în `rag/` și pune acolo PDF-urile.
3. Activează mediul virtual, apoi rulează:

```bash
python create_embeddings.py
```

> 🔹 Va apărea `Split into ... chunks`, dar **mai durează câteva minute** pentru salvarea efectivă a bazei de date Chroma DB.

---

### 🧠 6. Pornește serverul RAG API

```bash
start_rag.bat
```

> Așteaptă mesajul: `Application startup complete`.

---

### 🧠 7. LLM – Local sau Cloud

#### ✅ Local (fără costuri):

1. Instalează [Ollama](https://ollama.com/)
2. Rulează:

```bash
ollama pull mistral
```

#### 🌐 Cloud (API Microsoft):

- Se folosește o cheie API de la Microsoft Azure.
- Nu sunt necesare instalări suplimentare.
- Se configurează în cod – vezi documentația echipei.

---

## ✅ Recapitulare rapidă

1. Clonează repo-ul
2. Rulează backend Java (IntelliJ, SDK ≤ 1.5)
3. Configurează și activează mediu virtual Python
4. Instalează Tesseract + Poppler și configurează path-ul
5. Pune PDF-urile, rulează `create_embeddings.py` (durează!)
6. Pornește RAG API
7. Rulează LLM local sau cloud

---

## 📬 Contact echipă LLM

Pentru întrebări despre chatbot, RAG, embeddings sau LLM:

- Miruna *(Scrum Master)*
- Andrei
- Florea
- Ștefan

---

💬 Ready to chat smart.  
🧠 Powered by context.  
🚀 Dezvoltat de studenți FII!
