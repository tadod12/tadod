FROM python:3.12

WORKDIR /usr/app/src

RUN apt-get update && apt-get install -y \
    libsasl2-dev \
    python-dev-is-python3 \
    libldap2-dev \
    libssl-dev

COPY ./scripts .

RUN pip install --upgrade pip && \
    pip install --no-cache-dir -r requirements.txt && \
    chmod +x ./entrypoint.sh
