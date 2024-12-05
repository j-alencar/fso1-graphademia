document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('pesquisaForm').addEventListener('submit', function(event) {
        event.preventDefault();

        const name = document.getElementById('name').value;

        fetch(`/autores/pesquisar-autor/resultados?name=${name}`, { method: 'GET' })
        .then(response => response.json())
        .then(data => {
            if (data && Array.isArray(data) && data.length > 0) {
                let resultHtml = `
                    <h2 class="text-center mb-4">Resultados</h2>
                    <div class="row g-4">
                `;

                data.forEach(autor => {
                    const encodedData = encodeURIComponent(JSON.stringify(autor));
                    
                    resultHtml += `
                        <div class="col-md-4">
                            <div class="card shadow-sm">
                                <div class="card-body">
                                    <h5 class="card-title">${autor.name}</h5>
                                    <p class="card-text"><strong>ID:</strong> ${autor.authorId}</p>
                                    <p class="card-text"><strong>DBLP:</strong> ${autor.dblp || 'N/A'}</p>
                                    <p class="card-text"><strong>ORCID:</strong> ${autor.orcid || 'N/A'}</p>
                                    <p class="card-text"><strong>H-Index:</strong> ${autor.hindex || 'N/A'}</p>
                                    <button class="btn btn-success w-100" data-autor="${encodedData}" onclick="salvarAutor(this)">Salvar</button>
                                </div>
                            </div>
                        </div>
                    `;
                });

                resultHtml += '</div>';
                document.getElementById('resultado').innerHTML = resultHtml;
            } else {
                document.getElementById('resultado').innerHTML = `
                    <div class="alert alert-warning text-center" role="alert">
                        Nenhum resultado encontrado.
                    </div>
                `;
            }
        })
        .catch(error => {
            console.error('Erro:', error);
            alertarToast("Erro ao pesquisar o autor.", "danger");
        });
    });
});

function salvarAutor(button) {
    const autorData = decodeURIComponent(button.getAttribute('data-autor'));

    if (!autorData || autorData.trim() === '') {
        alertarToast("Dados do autor estão vazios ou inválidos.", "warning");
        return;
    }

    try {
        const autor = JSON.parse(autorData);

        fetch('/autores/salvar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(autor)
        })
        .then(response => response.json())
        .then(data => {
            if (data.status === 'ok') {
                alertarToast("Autor salvo com sucesso!", "success");
            } else {
                alertarToast("Erro ao salvar o autor.", "danger");
            }
        })
        .catch(error => {
            console.error('Erro:', error);
            alertarToast("Erro ao salvar o autor.", "danger");
        });
    } catch (error) {
        console.error("Erro ao parsear o JSON:", error);
        alertarToast("Erro ao salvar o autor. Dados inválidos.", "danger");
    }
}

function alertarToast(msg, type = 'info') {
    const containerToast = document.getElementById('containerToast');

    const toast = document.createElement('div');
    toast.className = `toast align-items-center text-bg-${type} border-0`;
    toast.setAttribute('role', 'alert');
    toast.setAttribute('aria-live', 'assertive');
    toast.setAttribute('aria-atomic', 'true');
    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">
                ${msg}
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
    `;

    containerToast.appendChild(toast);

    const bsToast = new bootstrap.Toast(toast, { delay: 3000 }); // 3 segundos até o fade out
    bsToast.show();

    toast.addEventListener('hidden.bs.toast', () => {
        toast.remove();
    });
}
