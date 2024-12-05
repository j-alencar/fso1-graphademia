document.addEventListener('DOMContentLoaded', function () {
    const botaoAutoPop = document.getElementById('autopopularBtn');

    botaoAutoPop.addEventListener('click', function () {
        const obraId = botaoAutoPop.getAttribute('data-obra-id');
        autoPopular(obraId); 
    });
});

function autoPopular(obraId) {
    fetch(`/obras/${obraId}/autopopular`, {
        method: 'GET'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Resposta da rede não foi OK');
        }
        return response.json();
    })
    .then(data => {
        if (data.status === 'ok') {
            console.log("Atualizei a obra:", data.data);
            atualizarCampos(data.data);
            mostrarToast("Campos de obra populados!", "success");
        } else {
            mostrarToast("Erro populando Obra: " + data.msg, "danger");
        }
    })
    .catch(error => {
        console.error("Error durante autoPopObra:", error);
        mostrarToast("Falha ao popular campos da Obra.", "danger");
    });
}

function mostrarToast(msg, type) {
    const containerToast = document.getElementById('containerToast');
    const toast = document.getElementById('msgToast');

    const toastBody = toast.querySelector('.toast-body');

    toastBody.textContent = msg;
    toast.classList.remove('bg-success', 'bg-danger');
    toast.classList.add(type === 'success' ? 'bg-success' : 'bg-danger');

    const instToast = new bootstrap.Toast(toast);
    instToast.show();
}

function atualizarCampos(obra) {
    document.getElementById('title').value = obra.title || '';
    document.getElementById('year').value = obra.year || '';
    document.getElementById('doi').value = obra.doi || '';
    document.getElementById('publicationVenueName').value = obra.publicationVenueName || '';
    document.getElementById('publicationVenueType').value = obra.publicationVenueType || '';
    document.getElementById('url').value = obra.url || '';
    document.getElementById('tldr').value = obra.tldr || '';
}
