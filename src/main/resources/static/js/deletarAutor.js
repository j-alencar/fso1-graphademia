function deletarAutor(id) {
    if (confirm("Tem certeza que deseja deletar este autor?")) {
        fetch(`/autores/${id}`, {
            method: 'DELETE',
        })
        .then(response => {
            if (response.ok) {
                alert("Autor deletado com sucesso!");
                location.reload();
            } else {
                response.text().then(errorMsg => alert(errorMsg));
            }
        })
        .catch(error => console.error("Erro ao deletar autor:", error));
    }
}
