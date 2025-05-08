async function sendMessage() {
    const inputField = document.getElementById('user-input');
    const providerToggle = document.getElementById('provider-toggle');
    const message = inputField.value.trim();
    const provider = providerToggle.checked ? "microsoft" : "mistral";
    const chatBox = document.getElementById('chat-box');

    if (!message) return;

    chatBox.innerHTML += `<div class="message user"><strong>Tu:</strong> ${message}</div>`;
    inputField.value = '';
    chatBox.scrollTop = chatBox.scrollHeight;

    try {
        const response = await fetch('http://localhost:8081/chat', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                message: message,
                provider: provider
            })
        });

        const data = await response.json();

        chatBox.innerHTML += `<div class="message bot"><strong>Model:</strong> ${data.answer}</div>`;

        if (data.chunks && data.chunks.length > 0) {
            chatBox.innerHTML += `<div class="message bot"><strong>Surse:</strong></div>`;
            data.chunks.forEach((chunk, index) => {
                chatBox.innerHTML += `<div class="message bot">(${index + 1}) ${chunk}</div>`;
            });
        }

        if (data.sources && data.sources.length > 0) {
            chatBox.innerHTML += `<div class="message bot"><strong>Chunks:</strong></div>`;
            data.sources.forEach((source, index) => {
                chatBox.innerHTML += `<div class="message bot">(${index + 1}) ${source}</div>`;
            });
        }

        chatBox.scrollTop = chatBox.scrollHeight;
    } catch (error) {
        chatBox.innerHTML += `<div class="message bot"><strong>Model:</strong> Eroare la comunicare cu serverul.</div>`;
    }
}

window.onload = () => {
    const inputField = document.getElementById('user-input');
    inputField.focus();

    inputField.addEventListener('keydown', function(event) {
        if (event.key === 'Enter') {
            sendMessage();
        }
    });
};
