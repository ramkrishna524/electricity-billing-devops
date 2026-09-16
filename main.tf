terraform {

  backend "s3" {
    bucket = "electricity-billing-terraform-state"
    key    = "terraform.tfstate"
    region = "ap-south-1"
  }

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

data "aws_vpc" "default" {
  default = true
}

data "aws_subnet" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }

  availability_zone = "ap-south-1b"
}

resource "aws_instance" "electricity_billing" {
  ami           = var.ami_id
  instance_type = var.instance_type

  subnet_id = data.aws_subnet.default.id

  vpc_security_group_ids = [
    aws_security_group.electricity_billing.id
  ]

  iam_instance_profile = data.aws_iam_instance_profile.ec2_ecr_pull.name

  tags = {
    Name = "electricity-billing-devops"
  }
}

resource "aws_security_group" "electricity_billing" {
  name        = "launch-wizard-1"
  description = "launch-wizard-1 created 2026-09-15T04:50:46.605Z"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["157.50.144.228/32"]
  }

  ingress {
    from_port   = 8081
    to_port     = 8081
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

data "aws_iam_instance_profile" "ec2_ecr_pull" {
  name = "EC2-ElectricityBilling-ECR-Pull"
}

output "ec2_instance_id" {
  value = aws_instance.electricity_billing.id
}

output "ec2_public_ip" {
  value = aws_instance.electricity_billing.public_ip
}

output "ec2_public_dns" {
  value = aws_instance.electricity_billing.public_dns
}
