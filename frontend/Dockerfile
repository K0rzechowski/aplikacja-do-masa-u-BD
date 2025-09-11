FROM node:24.0.0-alpine

# Install Angular CLI globally
# RUN npm install -g @angular/cli@19

# Create and set working directory
WORKDIR /app

# Copy package files first for better caching
COPY package.json package-lock.json ./

# Copy the rest of the application files
# COPY . .

# Install remaining dependencies
# RUN npm install
COPY run.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/run.sh;

# Expose port
EXPOSE 4200

ENTRYPOINT ["/usr/local/bin/run.sh"]

# Start Angular dev server
# CMD ["ng", "serve", "--host", "0.0.0.0", "--port", "4200"]