const statsElements = {
    total: document.getElementById("total-artworks"),
    available: document.getElementById("available-artworks"),
    sold: document.getElementById("sold-artworks"),
    featured: document.getElementById("featured-artworks")
};

const featuredGrid = document.getElementById("featured-grid");
const galleryGrid = document.getElementById("gallery-grid");
const filterForm = document.getElementById("filter-form");
const artworkForm = document.getElementById("artwork-form");
const formMessage = document.getElementById("form-message");
const resetFormButton = document.getElementById("reset-form");
const buyerLoginForm = document.getElementById("buyer-login-form");
const painterLoginForm = document.getElementById("painter-login-form");
const authMessage = document.getElementById("auth-message");
const authStatus = document.getElementById("auth-status");
const logoutButton = document.getElementById("logout-button");
const adminSupportText = document.getElementById("admin-support-text");
const FALLBACK_IMAGE_SVG = encodeURIComponent(`
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 600 400">
  <rect width="600" height="400" fill="#f6eee4"/>
  <rect x="34" y="34" width="532" height="332" rx="28" fill="#fff8f0" stroke="#d8c4b6"/>
  <circle cx="186" cy="162" r="54" fill="#d48452" opacity="0.75"/>
  <path d="M110 300l92-94 62 62 88-100 138 132H110z" fill="#9f3d2f" opacity="0.85"/>
  <text x="300" y="90" text-anchor="middle" font-family="Arial, sans-serif" font-size="26" fill="#6f2117">Artwork Preview</text>
  <text x="300" y="336" text-anchor="middle" font-family="Arial, sans-serif" font-size="18" fill="#6f2117">Image unavailable from provided URL</text>
</svg>
`);
const FALLBACK_IMAGE_URL = `data:image/svg+xml;charset=UTF-8,${FALLBACK_IMAGE_SVG}`;
let currentUser = null;

function currency(value) {
    return new Intl.NumberFormat("en-IN", {
        style: "currency",
        currency: "USD"
    }).format(value);
}

function renderArtworkCard(artwork) {
    const canBuy = currentUser && currentUser.role === "BUYER" && artwork.status === "AVAILABLE";
    const actionMarkup = artwork.status === "AVAILABLE"
        ? `<button class="button button-primary buy-button" type="button" data-artwork-id="${artwork.id}" data-artwork-title="${escapeHtml(artwork.title)}">${canBuy ? "Buy Now" : "Login as Buyer"}</button>`
        : `<span class="pill pill-muted">Sold</span>`;

    return `
        <article class="artwork-card">
            <img src="${artwork.imageUrl}" alt="${artwork.title}" loading="lazy" onerror="handleImageError(this, '${escapeHtml(artwork.title)}')">
            <div class="artwork-card-body">
                <p class="section-kicker">${artwork.category}</p>
                <h4>${artwork.title}</h4>
                <p class="artwork-meta">by ${artwork.artistName}</p>
                <p class="artwork-description">${artwork.description}</p>
                <div class="artwork-footer">
                    <span class="pill">${artwork.status}</span>
                    <span class="price">${currency(artwork.price)}</span>
                </div>
                <div class="artwork-actions">
                    ${actionMarkup}
                </div>
            </div>
        </article>
    `;
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll("\"", "&quot;")
        .replaceAll("'", "&#39;");
}

window.handleImageError = function handleImageError(imageElement, title) {
    if (imageElement.dataset.fallbackApplied === "true") {
        return;
    }

    imageElement.dataset.fallbackApplied = "true";
    imageElement.src = FALLBACK_IMAGE_URL;
    imageElement.alt = `${title} preview unavailable`;
};

function renderGrid(gridElement, artworks, emptyMessage) {
    if (!artworks.length) {
        gridElement.innerHTML = `<div class="empty-state">${emptyMessage}</div>`;
        return;
    }

    gridElement.innerHTML = artworks.map(renderArtworkCard).join("");
}

async function handlePurchase(artworkId, artworkTitle, buttonElement) {
    if (!currentUser || currentUser.role !== "BUYER") {
        window.alert("Please log in as a buyer to purchase artwork.");
        return;
    }

    buttonElement.disabled = true;
    buttonElement.textContent = "Processing...";

    try {
        const response = await fetch(`/api/gallery/artworks/${artworkId}/purchase`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                customerName: currentUser.name,
                customerEmail: currentUser.email
            })
        });

        const body = await response.json().catch(() => ({}));
        if (!response.ok) {
            throw new Error(body.message || "Unable to complete purchase.");
        }

        window.alert(`Purchase complete for "${body.artworkTitle}".`);
        await Promise.all([loadStats(), loadFeatured(), loadGallery()]);
    } catch (error) {
        window.alert(error.message || "Unable to complete purchase.");
        buttonElement.disabled = false;
        buttonElement.textContent = "Buy Now";
    }
}

async function loadStats() {
    const response = await fetch("/api/gallery/stats");
    const stats = await response.json();

    statsElements.total.textContent = stats.totalArtworks;
    statsElements.available.textContent = stats.availableArtworks;
    statsElements.sold.textContent = stats.soldArtworks;
    statsElements.featured.textContent = stats.featuredArtworks;
}

async function loadFeatured() {
    const response = await fetch("/api/gallery/artworks?featured=true&size=3");
    const data = await response.json();
    renderGrid(featuredGrid, data.content || [], "No featured artwork is available right now.");
}

async function loadGallery(params = new URLSearchParams({ page: "0", size: "9" })) {
    const response = await fetch(`/api/gallery/artworks?${params.toString()}`);
    const data = await response.json();
    renderGrid(galleryGrid, data.content || [], "No artworks matched the current filter.");
}

async function initializePage() {
    try {
        await loadCurrentUser();
        await Promise.all([loadStats(), loadFeatured(), loadGallery()]);
    } catch (error) {
        galleryGrid.innerHTML = `<div class="empty-state">Unable to load the gallery right now.</div>`;
        featuredGrid.innerHTML = `<div class="empty-state">Unable to load featured artworks right now.</div>`;
    }
}

function applyAuthState() {
    const isPainter = currentUser && currentUser.role === "PAINTER";
    const isBuyer = currentUser && currentUser.role === "BUYER";

    authStatus.textContent = currentUser
        ? `${currentUser.name} logged in as ${currentUser.role.toLowerCase()}`
        : "Not logged in";
    logoutButton.hidden = !currentUser;
    artworkForm.hidden = !isPainter;
    adminSupportText.textContent = isPainter
        ? `Logged in as painter ${currentUser.name}. New paintings will be published under your name.`
        : "Login as a painter to add new artwork.";

    if (isPainter) {
        document.getElementById("artistName").value = currentUser.name;
    } else {
        artworkForm.reset();
        document.getElementById("artistName").value = "";
    }

    if (isBuyer) {
        authMessage.className = "form-message success";
        authMessage.textContent = "Buyer login active. You can purchase available artworks.";
    }
}

async function loadCurrentUser() {
    try {
        const response = await fetch("/api/auth/me");
        if (!response.ok) {
            currentUser = null;
            applyAuthState();
            return;
        }

        const data = await response.json();
        currentUser = data.user;
    } catch (error) {
        currentUser = null;
    }

    applyAuthState();
}

async function login(email, password) {
    const response = await fetch("/api/auth/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ email, password })
    });

    const body = await response.json().catch(() => ({}));
    if (!response.ok) {
        throw new Error(body.message || "Login failed.");
    }

    currentUser = body.user;
    applyAuthState();
    await Promise.all([loadFeatured(), loadGallery()]);
    return body;
}

filterForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const params = new URLSearchParams({
        page: "0",
        size: "9"
    });

    const category = document.getElementById("category-filter").value.trim();
    const featuredOnly = document.getElementById("featured-filter").checked;

    if (category) {
        params.set("category", category);
    }

    if (featuredOnly) {
        params.set("featured", "true");
    }

    try {
        await loadGallery(params);
    } catch (error) {
        galleryGrid.innerHTML = `<div class="empty-state">Unable to apply that filter right now.</div>`;
    }
});

artworkForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    formMessage.className = "form-message";
    formMessage.textContent = "Creating artwork...";

    const payload = {
        title: document.getElementById("title").value.trim(),
        artistName: document.getElementById("artistName").value.trim(),
        category: document.getElementById("category").value.trim(),
        description: document.getElementById("description").value.trim(),
        imageUrl: document.getElementById("imageUrl").value.trim(),
        price: Number(document.getElementById("price").value),
        featured: document.getElementById("featured").checked,
        status: document.getElementById("status").value
    };

    try {
        const response = await fetch("/api/admin/artworks", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const errorBody = await response.json().catch(() => ({}));
            const message = errorBody.message || "Unable to create artwork.";
            throw new Error(message);
        }

        artworkForm.reset();
        formMessage.className = "form-message success";
        formMessage.textContent = "Artwork created successfully.";
        await Promise.all([loadStats(), loadFeatured(), loadGallery()]);
    } catch (error) {
        formMessage.className = "form-message error";
        formMessage.textContent = error.message || "Something went wrong while saving artwork.";
    }
});

resetFormButton.addEventListener("click", () => {
    artworkForm.reset();
    formMessage.className = "form-message";
    formMessage.textContent = "";
});

document.addEventListener("click", async (event) => {
    const button = event.target.closest(".buy-button");
    if (!button) {
        return;
    }

    await handlePurchase(
        button.dataset.artworkId,
        button.dataset.artworkTitle,
        button
    );
});

buyerLoginForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    authMessage.className = "form-message";
    authMessage.textContent = "Signing in buyer...";

    try {
        await login(
            document.getElementById("buyer-email").value.trim(),
            document.getElementById("buyer-password").value
        );
        authMessage.className = "form-message success";
        authMessage.textContent = "Buyer login successful.";
    } catch (error) {
        authMessage.className = "form-message error";
        authMessage.textContent = error.message || "Buyer login failed.";
    }
});

painterLoginForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    authMessage.className = "form-message";
    authMessage.textContent = "Signing in painter...";

    try {
        await login(
            document.getElementById("painter-email").value.trim(),
            document.getElementById("painter-password").value
        );
        authMessage.className = "form-message success";
        authMessage.textContent = "Painter login successful. You can now add paintings.";
    } catch (error) {
        authMessage.className = "form-message error";
        authMessage.textContent = error.message || "Painter login failed.";
    }
});

logoutButton.addEventListener("click", async () => {
    await fetch("/api/auth/logout", { method: "POST" });
    currentUser = null;
    applyAuthState();
    authMessage.className = "form-message";
    authMessage.textContent = "Logged out successfully.";
    await Promise.all([loadFeatured(), loadGallery()]);
});

initializePage();
