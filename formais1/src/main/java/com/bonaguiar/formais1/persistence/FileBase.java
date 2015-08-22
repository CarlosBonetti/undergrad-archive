package com.bonaguiar.formais1.persistence;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileBase<T> {
	private String filename;

	public FileBase(String filename) {
		this.filename = filename;
	}

	/**
	 * Salva no arquivo o objeto, sobrescrevendo a base
	 *
	 * @param hash
	 * @throws IOException
	 */
	protected void save(T obj) throws IOException {
		FileOutputStream fos = new FileOutputStream(filename);
		ObjectOutputStream output = new ObjectOutputStream(fos);
		output.writeObject(obj);
		output.flush();
	}

	/**
	 * Retorna o objeto do arquivo
	 *
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	protected T get() throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(filename);
		ObjectInputStream input = new ObjectInputStream(fis);
		T obj = (T) input.readObject();
		return obj;
	}
}
