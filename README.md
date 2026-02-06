# 🚗 Car Sharing Service API

An automated management system for a modern car sharing service. This project transforms an outdated paper-based system into a high-performance RESTful API, streamlining car inventory, user rentals, and automated payments.

## 🌟 Key Features

* **🛡️ Secure Authentication**: JWT-based login and registration with role-based access control (Manager vs. Customer).
* **🏎️ Inventory Management**: Real-time tracking of car availability and detailed specifications.
* **📅 Rental Lifecycle**: Automated rental tracking, including overdue monitoring and instant inventory updates.
* **💳 Stripe Integration**: Seamless credit card payments and fine calculation for overdue rentals.
* **🤖 Telegram Notifications**: Instant alerts for new rentals, successful payments, and daily overdue reports.
* **🐳 Dockerized**: Fully containerized environment for easy deployment.

---

## 🏗️ System Architecture

The system follows a layered architecture, ensuring separation of concerns between the API layer, business logic, and external integrations.

```mermaid
graph TD
    subgraph Client_Layer
        User((User/Admin))
    end

    subgraph API_Layer
        AC[Auth Controller]
        CC[Car Controller]
        RC[Rental Controller]
        PC[Payment Controller]
        UC[User Controller]
    end

    subgraph Service_Layer
        RS[Rental Service]
        CS[Car Service]
        PayS[Payment Service]
        NS[Notification Service]
    end

    subgraph External_Integrations
        Stripe((Stripe API))
        Telegram((Telegram Bot))
    end

    subgraph Data_Layer
        DB[(MySQL Database)]
        JPA[Spring Data JPA]
    end

    User --> API_Layer
    API_Layer --> Service_Layer
    Service_Layer --> JPA
    JPA --> DB
    
    RS --> NS
    PayS --> Stripe
    PayS --> NS
    NS --> Telegram

```

---

## 📊 Database Schema

The following diagram illustrates the relationships between users, cars, rentals, and payments.

```mermaid
erDiagram
    USER ||--o{ RENTAL : makes
    CAR ||--o{ RENTAL : "is rented"
    RENTAL ||--|| PAYMENT : "has one"

    USER {
        Long id
        String email
        String password
        Enum role
    }

    CAR {
        Long id
        String model
        String brand
        Enum type
        Integer inventory
        Decimal daily_fee
    }

    RENTAL {
        Long id
        LocalDate rental_date
        LocalDate return_date
        LocalDate actual_return_date
        Long car_id
        Long user_id
    }

    PAYMENT {
        Long id
        Enum status
        Enum type
        String session_url
        String session_id
        Decimal amount
    }

```

---

## 🚀 Getting Started

### Prerequisites

* **Java 17**
* **Docker & Docker Compose**
* **Stripe Account** (for API keys)
* **Telegram Bot** (via BotFather)

### Installation

1. **Clone the repository:**
```bash
git clone https://github.com/your-org/car-sharing-app.git
cd car-sharing-app

```


2. **Configure Environment Variables:**
   Create a `.env` file in the root directory based on `.env.sample`:
```env
MYSQL_ROOT_PASSWORD=your_pass
STRIPE_SECRET_KEY=your_stripe_key
TELEGRAM_BOT_TOKEN=your_bot_token
TELEGRAM_CHAT_ID=your_chat_id

```


3. **Run with Docker:**
```bash
docker-compose up --build

```



---

## 🛠️ API Endpoints

| Category | Method | Endpoint | Description |
| --- | --- | --- | --- |
| **Auth** | `POST` | `/register` | Register a new user |
| **Cars** | `GET` | `/cars` | List all available cars |
| **Rentals** | `POST` | `/rentals` | Create a new rental |
| **Rentals** | `POST` | `/rentals/{id}/return` | Return a car |
| **Payments** | `POST` | `/payments` | Create Stripe session |
| **Users** | `GET` | `/users/me` | Get profile information |
