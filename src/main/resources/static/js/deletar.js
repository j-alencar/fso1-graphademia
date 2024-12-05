function mostrarModal(msg, onConfirm) {
    const modalHtml = `
        <div class="modal fade" id="modalConfirmacao" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="modalLabel">Confirmação</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        ${msg}
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="button" class="btn btn-danger" id="confirmAction">Confirmar</button>
                    </div>
                </div>
            </div>
        </div>
    `;

    // Criar modal
    const containerModal = document.createElement('div');
    containerModal.innerHTML = modalHtml;
    document.body.appendChild(containerModal);
    const modalConfirmacao = new bootstrap.Modal(document.getElementById('modalConfirmacao'));
    modalConfirmacao.show();

    document.getElementById('confirmAction').addEventListener('click', () => {
        modalConfirmacao.hide();
        onConfirm();
        containerModal.remove();
    });

    // Remover elemento
    const elementoModal = document.getElementById('modalConfirmacao');
    elementoModal.addEventListener('hidden.bs.modal', () => {
        containerModal.remove();
    });
}

function mostrarToast(msg, type = 'info') {
    const containerToast = document.getElementById('containerToast');
    if (!containerToast) {
        console.error("Toast container not found!");
        return;
    }

    const toastHtml = `
        <div class="toast align-items-center text-bg-${type} border-0" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body">
                    ${msg}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>
    `;

    const elementoToast = document.createElement('div');
    elementoToast.innerHTML = toastHtml;
    containerToast.appendChild(elementoToast);

    const toastElement = elementoToast.querySelector('.toast');
    const toast = new bootstrap.Toast(toastElement, { delay: 3000 });
    toast.show();

    toastElement.addEventListener('hidden.bs.toast', () => {
        elementoToast.remove();
    });
}

function deletarObra(id) {
    mostrarModal("Tem certeza que deseja deletar esta obra?", () => {
        fetch(`/obras/${id}`, {
            method: 'DELETE',
        })
        .then(response => {
            if (response.ok) {
                mostrarToast("Obra deletada com sucesso!", "success");
                location.reload();
            } else {
                response.text().then(errorMsg => mostrarToast(errorMsg, "danger"));
            }
        })
        .catch(error => {
            console.error("Erro ao deletar obra:", error);
            mostrarToast("Erro ao deletar obra.", "danger");
        });
    });
}

function deletarAutor(id) {
    mostrarModal("Tem certeza que deseja deletar este autor?", () => {
        fetch(`/autores/${id}`, {
            method: 'DELETE',
        })
        .then(response => {
            if (response.ok) {
                mostrarToast("Autor deletado com sucesso!", "success");
                location.reload();
            } else {
                response.text().then(errorMsg => mostrarToast(errorMsg, "danger"));
            }
        })
        .catch(error => {
            console.error("Erro ao deletar autor", error);
            mostrarToast("Erro ao deletar autor.", "danger");
        });
    });
}
