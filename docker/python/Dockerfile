FROM python:3.12

WORKDIR /usr/app/src

RUN apt-get update && apt-get install -y \
    libsasl2-dev \
    python-dev-is-python3 \
    libldap2-dev \
    libssl-dev \
    librdkafka-dev

COPY ./scripts .

RUN pip install --upgrade pip && \
    pip install pyarrow && \
    pip install pandas && \
    pip install confluent-kafka && \
    chmod +x ./entrypoint.sh
