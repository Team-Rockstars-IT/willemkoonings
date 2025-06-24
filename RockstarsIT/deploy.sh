#!/bin/bash

# ========= Defaults ==========
APP_PORT=8080
CLEAN_START=false
ENABLE_SWAGGER=true
# ===========================

# Store PIDs for cleanup
PIDS=()

# Function to get Docker assigned port
get_docker_port() {
    local container_name=$1
    local container_port=$2
    docker port "$container_name" "$container_port" 2>/dev/null | cut -d: -f2
}

# Cleanup function
cleanup() {
    echo ""
    echo "🛑 Shutting down all services..."

    # Stop Docker containers
    echo "🐳 Stopping Docker containers..."
    docker-compose down 2>/dev/null || true

    # Kill all processes gracefully first
    for pid in "${PIDS[@]}"; do
        if kill -0 "$pid" 2>/dev/null; then
            echo "🔄 Stopping process $pid..."
            kill -TERM "$pid" 2>/dev/null
        fi
    done

    # Wait a bit for graceful shutdown
    sleep 3

    # Force kill any remaining processes
    for pid in "${PIDS[@]}"; do
        if kill -0 "$pid" 2>/dev/null; then
            echo "💀 Force killing process $pid..."
            kill -KILL "$pid" 2>/dev/null
        fi
    done

    echo "✅ All services stopped!"
    exit 0
}

# Trap Ctrl+C and other signals
trap cleanup SIGINT SIGTERM EXIT

# ========= Parse Named Arguments ==========
for arg in "$@"
do
  case $arg in
    --app-port=*)
      APP_PORT="${arg#*=}"
      shift
      ;;
    --swagger=*)
      ENABLE_SWAGGER="${arg#*=}"
      shift
      ;;
    --clean)
      CLEAN_START=true
      shift
      ;;
    *)
      echo "Unknown argument: $arg"
      echo "Available options:"
      echo "  --app-port=PORT         Application port (REQUIRED)"
      echo "  --swagger=true/false    Enable Swagger UI (default: true)"
      echo "  --clean                 Start with clean database state"
      exit 1
      ;;
  esac
done
# ==========================================

echo "🚀 Starting RockstarsIT Deployment"
echo "🔧 Swagger enabled: $ENABLE_SWAGGER on port $APP_PORT"
echo "🌐 Application port: $APP_PORT"

# Handle clean start
if [ "$CLEAN_START" = true ]; then
    echo "🧹 Starting with clean database state..."
    echo "🗑️  Removing PostgreSQL container and volumes..."
    docker-compose down -v 2>/dev/null || true
    echo "✅ Clean state ready!"
fi

echo "🐳 Starting PostgreSQL with random port..."
docker-compose up -d rockstarsit-db

echo "⏳ Waiting for PostgreSQL container to start..."
sleep 5

# Get the randomly assigned port
POSTGRES_PORT=$(get_docker_port "rockstarsit-database" "5432")

# Check if we got the port
if [ -z "$POSTGRES_PORT" ]; then
    echo "❌ Failed to get PostgreSQL port. Checking container status..."
    docker ps -a | grep rockstarsit
    docker-compose logs rockstarsit-db
    exit 1
fi

echo "🐳 Docker assigned PostgreSQL port: $POSTGRES_PORT"

echo "⏳ Waiting for PostgreSQL to be ready..."
for attempt in {1..30}; do
    # Use correct container name from docker-compose.yml
    if docker exec rockstarsit-database pg_isready -U rockstarsit_user -d rockstarsit >/dev/null 2>&1; then
        echo "✅ PostgreSQL is ready!"
        break
    fi
    echo "🔄 Attempt $attempt/30 - PostgreSQL not ready yet..."
    sleep 2
    if [ $attempt -eq 30 ]; then
        echo "❌ PostgreSQL failed to become ready after 30 attempts"
        echo "🔍 Container logs:"
        docker-compose logs rockstarsit-db
        exit 1
    fi
done

echo "🔨 Building project..."
./mvnw clean package -DskipTests || { echo "❌ Build failed"; exit 1; }

echo "🚀 Starting RockstarsIT Application..."
SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:$POSTGRES_PORT/rockstarsit" \
SPRING_DATASOURCE_USERNAME="rockstarsit_user" \
SPRING_DATASOURCE_PASSWORD="rockstarsit_password" \
SPRING_JPA_HIBERNATE_DDL_AUTO="update" \
SERVER_PORT="$APP_PORT" \
java -jar target/RockstarsIT-*.jar &
APP_PID=$!
PIDS+=($APP_PID)

echo "⏳ Waiting for RockstarsIT Application to be ready..."
for attempt in {1..30}; do
    if curl -s "http://localhost:$APP_PORT/actuator/health" >/dev/null 2>&1; then
        echo "✅ RockstarsIT Application is ready on port $APP_PORT!"
        break
    fi
    echo "🔄 Attempt $attempt/30 - RockstarsIT Application not ready yet..."
    sleep 2
    if [ $attempt -eq 30 ]; then
        echo "❌ RockstarsIT Application failed to become ready after 30 attempts"
        exit 1
    fi
done

echo "✅ All services started!"
echo "🌐 Application: http://localhost:$APP_PORT--$APP_PID"
if [ "$ENABLE_SWAGGER" = true ]; then
    echo "📖 Swagger:     http://localhost:$APP_PORT/swagger-ui.html"
fi
echo "🐳 PostgreSQL:  localhost:$POSTGRES_PORT"

echo "⏳ Press [Ctrl+C] to stop all services..."

# Wait for all background jobs
wait "${PIDS[@]}"








