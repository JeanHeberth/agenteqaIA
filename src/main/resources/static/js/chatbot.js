const toggleButton = document.getElementById('chat-toggle');
const chatWindow = document.getElementById('chat-window');
const chatMessages = document.getElementById('chat-messages');
const messageInput = document.getElementById('message-input');
const notifySound = document.getElementById('notify-sound');
const typingIndicator = document.getElementById('typing-indicator');
const themeToggle = document.getElementById('toggle-theme');
const BASE_URL = 'https://agenteqaia-backend.onrender.com';

// Alternar janela do chat
toggleButton.onclick = () => {
    chatWindow.classList.toggle('open');
    chatWindow.style.display = chatWindow.classList.contains('open') ? 'flex' : 'none';
};

themeToggle.onclick = () => {
    document.body.classList.toggle('dark-mode');
};

messageInput.addEventListener('keydown', function (event) {
    if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault();
        sendMessage();
    }
});

async function sendMessage() {
    const userMsg = messageInput.value.trim();
    if (!userMsg) return;

    appendMessage('Você', userMsg);
    messageInput.value = '';
    typingIndicator.style.display = 'inline';

    try {
        const response = await fetch(`${BASE_URL}/conversa/pergunta`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({mensagem: userMsg})
        });

        const data = await response.json();
        typingIndicator.style.display = 'none';
        appendMessage('IA', data.resposta);
    } catch (error) {
        typingIndicator.style.display = 'none';
        appendMessage('Erro', 'Não foi possível enviar a mensagem.');
    }
}

function appendMessage(sender, text) {
    const div = document.createElement('div');
    const hora = new Date().toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'});

    let parsedContent = marked.parse(text);
    const fileUrlRegex = /(https?:\/\/[^\s"]+?\.(jpg|jpeg|png|pdf))/g;

    parsedContent = parsedContent.replace(fileUrlRegex, (url) => {
        if (url.match(/\.(jpg|jpeg|png)$/)) {
            return `<br><img src="${url}" alt="imagem" style="max-width: 180px; border-radius: 5px; margin-top: 5px;" />`;
        } else if (url.match(/\.pdf$/)) {
            return `<br><a href="${url}" target="_blank">📄 Abrir PDF</a>`;
        }
        return `<a href="${url}" target="_blank">${url}</a>`;
    });

    div.innerHTML = `<strong>${sender}:</strong> ${parsedContent}<div class="timestamp">${hora}</div>`;
    chatMessages.appendChild(div);
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

const recognition = new (window.SpeechRecognition || window.webkitSpeechRecognition)();
recognition.lang = 'pt-BR';
recognition.interimResults = false;
recognition.maxAlternatives = 1;

function iniciarReconhecimentoVoz() {
    recognition.start();
}

recognition.onresult = function (event) {
    const texto = event.results[0][0].transcript;
    messageInput.value = texto;
    sendMessage();
};

recognition.onerror = function (event) {
    console.error('Erro no reconhecimento de voz:', event.error);
};

async function uploadFile() {
    const input = document.getElementById('file-input');
    const file = input.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append('file', file);

    appendMessage('Você', `📎 Enviou: ${file.name}`);
    typingIndicator.style.display = 'inline';

    try {
        const response = await fetch(`${BASE_URL}/conversa/upload`, {
            method: 'POST',
            body: formData
        });

        const data = await response.text();
        typingIndicator.style.display = 'none';
        appendMessage('IA', data);
    } catch (error) {
        typingIndicator.style.display = 'none';
        appendMessage('Erro', 'Falha ao enviar o arquivo.');
    }

    input.value = '';
    document.getElementById('file-name').textContent = 'Nenhum arquivo';
}

document.getElementById('file-input').addEventListener('change', function () {
    const fileNameSpan = document.getElementById('file-name');
    fileNameSpan.textContent = this.files[0]?.name || 'Nenhum arquivo';
});

function handleDrop(event) {
    event.preventDefault();
    const file = event.dataTransfer.files[0];
    if (file && file.type.match(/^image\/(jpeg|png)$/)) {
        uploadDroppedFile(file);
    } else {
        alert("Apenas arquivos .jpg, .jpeg ou .png são aceitos via arrastar.");
    }
}

async function uploadDroppedFile(file) {
    const formData = new FormData();
    formData.append('file', file);

    appendMessage('Você', `📎 Enviou: ${file.name}`);
    typingIndicator.style.display = 'inline';

    try {
        const response = await fetch(`${BASE_URL}/conversa/upload`, {
            method: 'POST',
            body: formData
        });
        const data = await response.text();
        typingIndicator.style.display = 'none';
        appendMessage('IA', data);
    } catch (error) {
        typingIndicator.style.display = 'none';
        appendMessage('Erro', 'Falha ao enviar o arquivo.');
    }
}

const uploadContainer = document.getElementById('upload-container');
uploadContainer.addEventListener('dragover', () => uploadContainer.classList.add('dragover'));
uploadContainer.addEventListener('dragleave', () => uploadContainer.classList.remove('dragover'));
uploadContainer.addEventListener('drop', () => uploadContainer.classList.remove('dragover'));
