package com.ipartek.formacion.canciones.controller.canciones.backoffice;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ipartek.formacion.canciones.controller.canciones.Alert;
import com.ipartek.formacion.canciones.modelo.dao.GeneroDAO;
import com.ipartek.formacion.canciones.modelo.pojo.Genero;

/**
 * Servlet implementation class CancionesController
 */
@WebServlet("/backoffice/genero")
public class GeneroController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private int accion = Acciones.LISTAR;
	private GeneroDAO dao;

	private Alert alert; // Alertas para el usuario
	private String view; // vista para el forward
	private static final String VIEW_INDEX = "genero/index.jsp";
	private static final String VIEW_FORM = "genero/form.jsp";

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		dao = GeneroDAO.getInstance();
	}

	@Override
	public void destroy() {
		super.destroy();
		dao = null;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doProcess(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doProcess(request, response);
	}

	private void doProcess(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			alert = null;

			// determinar la accion a realizar
			accion = (request.getParameter("accion") != null) ? Integer.parseInt(request.getParameter("accion"))
					: Acciones.LISTAR;

			switch (accion) {
			case Acciones.LISTAR:
				listar(request);
				break;
			case Acciones.ELIMINAR:
				eliminar(request);
				break;
			case Acciones.MOSTRAR_FORMULARIO:
				mostrarFormulario(request);
				break;
			case Acciones.CREAR_MODIFICAR:
				crearModificar(request);
				break;

			default:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			request.setAttribute("alert", alert);
			request.getRequestDispatcher(view).forward(request, response);
		}

	}

	private void crearModificar(HttpServletRequest request) {
		// recoger parametros formulario
		int id = Integer.parseInt(request.getParameter("id"));
		String nombre = request.getParameter("nombre");

		Genero genero;

		genero = new Genero(id, nombre);

		if (id == -1) {
			if (dao.create(genero)) {
				alert = new Alert(Alert.TIPO_SUCCESS, "Registro creado con exito");
				listar(request);
			} else {
				alert = new Alert(Alert.TIPO_DANGER, "No se puede crear el registro");
				request.setAttribute("genero", genero);
				view = VIEW_FORM;
			}
		} else {
			if (dao.update(genero, id)) {
				alert = new Alert(Alert.TIPO_SUCCESS, "Registro modificado con exito");
				listar(request);
			} else {
				alert = new Alert(Alert.TIPO_DANGER, "No se puede Modifir el registro");
				request.setAttribute("genero", genero);
				view = VIEW_FORM;
			}
		}

	}

	private void mostrarFormulario(HttpServletRequest request) {

		Genero genero = null;
		if (request.getParameter("id") == null) {
			genero = new Genero();
		} else {
			int id = Integer.parseInt(request.getParameter("id"));
			genero = dao.findById(id);
		}

		request.setAttribute("genero", genero);
		view = VIEW_FORM;
	}

	private void eliminar(HttpServletRequest request) {

		int id = Integer.parseInt(request.getParameter("id"));

		if (dao.delete(id)) {
			alert = new Alert(Alert.TIPO_SUCCESS, "Eliminada con Exito el Registro(" + id + ")");
		} else {
			alert = new Alert(Alert.TIPO_DANGER, "NO se Eliminó el Registro(" + id + ")");
		}

		listar(request);
	}

	private void listar(HttpServletRequest request) {
		request.setAttribute("listado", dao.findAll());
		view = VIEW_INDEX;
	}

}
