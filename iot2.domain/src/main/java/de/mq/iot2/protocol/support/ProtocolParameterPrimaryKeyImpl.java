package de.mq.iot2.protocol.support;

class ProtocolParameterPrimaryKeyImpl {
	final String name;
	final String protocol;

	ProtocolParameterPrimaryKeyImpl(final String name, final String protocol) {
		this.name = name;
		this.protocol = protocol;
	}
	
	ProtocolParameterPrimaryKeyImpl() {
		this(null,null);
	}

	private boolean missingKeyFields(final ProtocolParameterPrimaryKeyImpl primaryKey) {
		return (primaryKey.name == null) || (primaryKey.protocol == null);
	}

	@Override
	public int hashCode() {
		if (missingKeyFields(this)) {
			return super.hashCode();
		}
		return name.hashCode() + protocol.hashCode();
	}

	@Override
	public boolean equals(final Object object) {

		if (!(object instanceof ProtocolParameterPrimaryKeyImpl)) {
			return super.equals(object);

		}
		final var other = (ProtocolParameterPrimaryKeyImpl) object;

		if (missingKeyFields(this) || (missingKeyFields(other))) {
			return super.equals(object);
		}

		return other.name.equals(name) && other.protocol.equals(protocol);
	}
}
