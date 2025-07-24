FROM node:24.0.0-alpine

RUN npm install -g @angular/cli@19

WORKDIR /
RUN mkdir /app
WORKDIR /app

ENV APP_NAME app
ENV STYLE 'css'
ENV SSR 'N'

# Generate Angular app only during image build
RUN ng new $APP_NAME --style=$STYLE --ssr=$SSR --skip-install && \
    mv $APP_NAME/* ./ && \
    rm -rf $APP_NAME

# Install dependencies
RUN npm install

# Expose port
EXPOSE 4200

# Start Angular dev server
CMD ["ng", "serve", "--host", "0.0.0.0", "--port", "4200"]