document.addEventListener('DOMContentLoaded', function () {
    const searchInput = document.getElementById('clientSearch');
    const resultsList = document.getElementById('clientResults');
    const clientIdInput = document.getElementById('clientId');

    let debounceTimer;

    searchInput.addEventListener('keyup', function () {
        const query = this.value;

        clearTimeout(debounceTimer);

        debounceTimer = setTimeout(() => {
            if (query.length < 2) {
                resultsList.innerHTML = '';
                return;
            }

         fetch('/api/clients/search?query=' + encodeURIComponent(query))
                .then(res => res.json())
                .then(data => {
                    resultsList.innerHTML = '';

                    data.forEach(client => {
                        const li = document.createElement('li');
                        li.className = 'list-group-item list-group-item-action';
                        li.textContent = client.name;

                        li.onclick = () => {
                            searchInput.value = client.name;
                            clientIdInput.value = client.id;
                            resultsList.innerHTML = '';
                        };

                        resultsList.appendChild(li);
                    });
                });
        }, 300);
    });
});